///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21+
//DEPS io.quarkus.platform:quarkus-bom:3.32.3@pom
//DEPS io.quarkus:quarkus-picocli
//DEPS info.picocli:picocli:4.7.6
//Q:CONFIG quarkus.banner.enabled=false
//Q:CONFIG quarkus.log.level=WARN

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
    name = "build",
    mixinStandardHelpOptions = true,
    version = "build 1.0.0",
    description = "Build and push CamelQuarkusVoter modules using Maven and Quarkus container-image extension"
)
public class build implements Callable<Integer> {

    // ANSI color codes
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RESET = "\u001B[0m";

    @Option(
        names = {"-p", "--project"},
        description = "Specify which project(s) to build. Valid values: ${COMPLETION-CANDIDATES}. Default: ${DEFAULT-VALUE}",
        defaultValue = "all"
    )
    private ProjectType project;

    @Option(
        names = {"-n", "--native"},
        description = "Enable native compilation (default: ${DEFAULT-VALUE})"
    )
    private boolean nativeMode = false;

    @Option(
        names = {"--parallel"},
        description = "Build modules in parallel (default: ${DEFAULT-VALUE})"
    )
    private boolean parallel = false;

    @Option(
        names = {"-b", "--build-mode"},
        description = "Build location. Valid values: ${COMPLETION-CANDIDATES}. Default: ${DEFAULT-VALUE}",
        defaultValue = "openshift"
    )
    private BuildMode buildMode;

    @Option(
        names = {"-r", "--registry"},
        description = "Container registry for podman mode (default: ${DEFAULT-VALUE})",
        defaultValue = "quay.io"
    )
    private String registry;

    @Option(
        names = {"-g", "--group"},
        description = "Container image group/namespace for podman mode (default: ${DEFAULT-VALUE})",
        defaultValue = "kevindubois"
    )
    private String group;

    @Option(
        names = {"--push"},
        description = "Push images to registry in podman mode (default: ${DEFAULT-VALUE})"
    )
    private boolean push = false;

    enum ProjectType {
        all, ingester, processor, ui, twitter
    }

    enum BuildMode {
        openshift, podman
    }

    @Override
    public Integer call() throws Exception {
        printHeader();

        // Validate we're in the right directory
        if (!Files.exists(Path.of("pom.xml"))) {
            printError("Error: pom.xml not found. Please run this script from the project root directory.");
            return 1;
        }

        // Determine which modules to build
        List<String> modules = getModulesToBuild();

        printInfo("Build configuration:");
        printInfo("  Projects: " + String.join(", ", modules));
        printInfo("  Native mode: " + nativeMode);
        if (nativeMode) {
            printInfo("  Native build: container-based (Mandrel) for cross-platform compatibility");
        }
        printInfo("  Build mode: " + buildMode);
        printInfo("  Parallel: " + parallel);
        if (buildMode == BuildMode.podman) {
            printInfo("  Registry: " + registry);
            printInfo("  Group: " + group);
            printInfo("  Push: " + push);
        }
        System.out.println();

        // Build each module
        boolean allSuccess;
        if (parallel && modules.size() > 1) {
            allSuccess = buildModulesParallel(modules);
        } else {
            allSuccess = buildModulesSequential(modules);
        }

        if (allSuccess) {
            printSuccess("==========================================");
            printSuccess("All modules built and pushed successfully!");
            printSuccess("==========================================");
            System.out.println();
            
            if (buildMode == BuildMode.openshift) {
                System.out.println("Images are now available in the OpenShift internal registry:");
                for (String module : modules) {
                    System.out.println("  - image-registry.openshift-image-registry.svc:5000/cameldemo/cameldemo-" + module + ":1.0.2");
                }
                System.out.println();
                System.out.println("You can now deploy using: kubectl apply -k kubefiles/");
            } else {
                if (push) {
                    System.out.println("Images built and pushed to registry:");
                    for (String module : modules) {
                        System.out.println("  - " + registry + "/" + group + "/cameldemo-" + module + ":1.0.2");
                    }
                } else {
                    System.out.println("Images built locally with podman:");
                    for (String module : modules) {
                        System.out.println("  - " + group + "/cameldemo-" + module + ":1.0.2");
                    }
                    System.out.println();
                    System.out.println("To push images, add --push flag and ensure you're logged in to " + registry);
                }
            }
            
            return 0;
        } else {
            printError("Build failed!");
            return 1;
        }
    }

    private List<String> getModulesToBuild() {
        if (project == ProjectType.all) {
            return Arrays.asList("ingester", "processor", "ui");
        } else {
            return List.of(project.name());
        }
    }

    private boolean buildModulesSequential(List<String> modules) {
        for (String module : modules) {
            if (!buildModule(module)) {
                return false;
            }
        }
        return true;
    }

    private boolean buildModulesParallel(List<String> modules) {
        printInfo("Building " + modules.size() + " modules in parallel...");
        System.out.println();
        
        List<Thread> threads = new ArrayList<>();
        List<Boolean> results = new ArrayList<>();
        
        for (String module : modules) {
            results.add(false); // Initialize with false
            int index = results.size() - 1;
            
            Thread thread = new Thread(() -> {
                boolean success = buildModule(module);
                synchronized (results) {
                    results.set(index, success);
                }
            });
            threads.add(thread);
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                printError("Build interrupted: " + e.getMessage());
                return false;
            }
        }
        
        // Check if all builds succeeded
        return results.stream().allMatch(r -> r);
    }

    private boolean buildModule(String module) {
        printModuleHeader(module);

        // Check if module directory exists
        File moduleDir = new File(module);
        if (!moduleDir.exists() || !moduleDir.isDirectory()) {
            printError("Module directory not found: " + module);
            return false;
        }

        // Build Maven command
        List<String> command = new ArrayList<>();
        command.add("mvn");
        command.add("clean");
        command.add("package");

        // Add container image build properties based on build mode
        if (buildMode == BuildMode.openshift) {
            command.add("-Dquarkus.container-image.builder=openshift");
            command.add("-Dquarkus.container-image.build=true");
            command.add("-Dquarkus.container-image.push=true");
        } else { // podman
            command.add("-Dquarkus.container-image.builder=podman");
            command.add("-Dquarkus.container-image.build=true");
            command.add("-Dquarkus.container-image.registry=" + registry);
            command.add("-Dquarkus.container-image.group=" + group);
            command.add("-Dquarkus.podman.platform=linux/amd64");
            if (push) {
                command.add("-Dquarkus.container-image.push=true");
            }
        }

        // Add native profile if requested
        if (nativeMode) {
            command.add("-Pnative");
            // Always use container-based builds for native compilation
            // This ensures Linux binaries are built for container images (OpenShift/Podman)
            // and provides cross-platform compatibility (works on macOS, Windows, Linux)
            command.add("-Dquarkus.native.container-build=true");
            // Force AMD64 platform for compatibility with most cloud platforms
            command.add("-Dquarkus.native.container-runtime-options=--platform=linux/amd64");
        }

        // Execute the command
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(moduleDir);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Read and display output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                printSuccess("✓ Successfully built and pushed " + module);
                System.out.println();
                return true;
            } else {
                printError("✗ Failed to build " + module + " (exit code: " + exitCode + ")");
                return false;
            }

        } catch (Exception e) {
            printError("✗ Exception while building " + module + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void printHeader() {
        System.out.println("==========================================");
        System.out.println("CamelQuarkusVoter Build Script");
        System.out.println("==========================================");
        System.out.println();
    }

    private void printModuleHeader(String module) {
        System.out.println(BLUE + "==========================================");
        System.out.println("Building module: " + module);
        System.out.println("==========================================" + RESET);
    }

    private void printSuccess(String message) {
        System.out.println(GREEN + message + RESET);
    }

    private void printError(String message) {
        System.err.println(RED + message + RESET);
    }

    private void printInfo(String message) {
        System.out.println(YELLOW + message + RESET);
    }

    private void printWarning(String message) {
        System.out.println(YELLOW + "⚠️  " + message + RESET);
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new build()).execute(args);
        System.exit(exitCode);
    }
}

// Made with Bob
