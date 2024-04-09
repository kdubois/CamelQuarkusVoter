package com.kevindubois.cameldemo.client;

import java.util.ArrayList;
import java.util.List;

import com.kevindubois.cameldemo.Vote;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@ApplicationScoped
@Path("/getresults")
@RegisterRestClient(configKey="processor-api")
public interface ProcessorRestClient {

    @GET
    @Retry(maxRetries = 3, delay = 100)
    @Fallback(ProcessorRestClient.ProcessorFallback.class)
    List<Vote> getVotes();

    public static class ProcessorFallback implements FallbackHandler<List<Vote>> {

        private static final List<Vote> EMPTY_VOTE = new ArrayList<>();
        
        @Override
        public List<Vote> handle(ExecutionContext context) {
            return EMPTY_VOTE;
        }

    }
}

