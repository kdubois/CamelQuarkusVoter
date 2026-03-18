# ArgoCD Deployment

**Infrastructure** (operators, Kafka, PostgreSQL)
- Lives in `kubefiles/base/infrastructure`
- Auto-syncs with self-heal enabled
- Sync waves 0-3

**Application** (microservices)
- Lives in `kubefiles/base/application`  
- Auto-syncs without self-heal
- Sync wave 4

## Quick Start

Deploy everything at once:

```bash
kubectl apply -f kubefiles/argo/app-of-apps.yaml
```

Or deploy separately for more control:

```bash
# Infrastructure first
kubectl apply -f kubefiles/argo/infrastructure-application.yaml

# Then your apps
kubectl apply -f kubefiles/argo/application-application.yaml
```

## Sync Waves

Resources deploy in order:
- Wave 0: Operators
- Wave 1: Knative Serving  
- Wave 2: Kafka
- Wave 3: PostgreSQL
- Wave 4: Your apps

## Using OpenShift Registry

By default we're using pre-built images from quay.io. If you want to use OpenShift's internal registry (don't forget to build/push the images first):

```bash
kubectl patch application camelquarkusvoter-application \
  -n openshift-gitops \
  --type merge \
  -p '{"spec":{"source":{"path":"kubefiles/overlays/application-openshift"}}}'
```

## Check Status

```bash
kubectl get applications -n openshift-gitops
argocd app list
```

## Troubleshooting

**Infrastructure stuck?**
```bash
kubectl get csv -n openshift-serverless
kubectl get csv -n openshift-operators | grep strimzi
argocd app sync camelquarkusvoter-infrastructure --prune
```

**Apps not starting?**
```bash
kubectl get kafka,knativeserving,pods -n cameldemo
argocd app sync camelquarkusvoter-application
```

## Structure

```
kubefiles/
├── argo/
│   ├── app-of-apps.yaml                 # Deploy this
│   ├── infrastructure-application.yaml  # Or this first
│   └── application-application.yaml     # Then this
├── base/
│   ├── infrastructure/                  # Operators, Kafka, DB
│   └── application/                     # Your microservices
└── overlays/
    └── application-openshift/           # OpenShift registry