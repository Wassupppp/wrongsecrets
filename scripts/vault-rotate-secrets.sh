#!/usr/bin/env bash
set -euo pipefail
 
VAULT_ADDR=${VAULT_ADDR:-}
VAULT_TOKEN=${VAULT_TOKEN:-}
VAULT_KV_MOUNT=${VAULT_KV_MOUNT:-secret}
VAULT_ROTATION_INTERVAL_SECONDS=${VAULT_ROTATION_INTERVAL_SECONDS:-0}
 
if [[ -z "$VAULT_ADDR" ]]; then
  echo "Error: VAULT_ADDR must be set (example: http://127.0.0.1:8200)" >&2
  exit 1
fi
 
if [[ -z "$VAULT_TOKEN" ]]; then
  echo "Error: VAULT_TOKEN must be set" >&2
  exit 1
fi
 
for command_name in curl openssl; do
  if ! command -v "$command_name" >/dev/null 2>&1; then
    echo "Error: $command_name is required" >&2
    exit 1
  fi
done
 
if ! [[ "$VAULT_ROTATION_INTERVAL_SECONDS" =~ ^[0-9]+$ ]]; then
  echo "Error: VAULT_ROTATION_INTERVAL_SECONDS must be a non-negative integer" >&2
  exit 1
fi
 
VAULT_ADDR=${VAULT_ADDR%/}
CURL_ARGS=(
  --silent
  --show-error
  --fail-with-body
  --request POST
  --header "Content-Type: application/json"
  --header "X-Vault-Token: $VAULT_TOKEN"
)
 
if [[ -n "${VAULT_NAMESPACE:-}" ]]; then
  CURL_ARGS+=(--header "X-Vault-Namespace: $VAULT_NAMESPACE")
fi
 
if [[ -n "${VAULT_CACERT:-}" ]]; then
  CURL_ARGS+=(--cacert "$VAULT_CACERT")
fi
 
if [[ "${VAULT_SKIP_VERIFY:-false}" == "true" ]]; then
  CURL_ARGS+=(--insecure)
fi
 
random_secret() {
  openssl rand -base64 32
}
 
write_secret() {
  local path=$1
  local key=$2
  local value=$3
  local payload
 
  payload=$(printf '{"data":{"%s":"%s"}}' "$key" "$value")
  echo "Rotating Vault secret at $VAULT_KV_MOUNT/$path"
  curl "${CURL_ARGS[@]}" \
    --data "$payload" \
    "$VAULT_ADDR/v1/$VAULT_KV_MOUNT/data/$path" >/dev/null
}
 
rotate_all_secrets() {
  write_secret "secret-challenge" "vaultpassword.password" "$(random_secret)"
  write_secret "injected" "vaultinjected.value" "$(random_secret)"
  write_secret "codified" "challenge47secret" "$(random_secret)"
  echo "Vault secrets rotated. The application will reload them automatically."
}
 
rotate_all_secrets
 
while ((VAULT_ROTATION_INTERVAL_SECONDS > 0)); do
  echo "Next rotation in $VAULT_ROTATION_INTERVAL_SECONDS seconds."
  sleep "$VAULT_ROTATION_INTERVAL_SECONDS"
  rotate_all_secrets
done