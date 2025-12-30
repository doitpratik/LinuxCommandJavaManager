# Linux Command Java Manager

Spring Boot utility for executing Linux commands in a controlled, policy-driven way. The service
supports two execution modes:

- **EXECUTABLE**: invoke allowlisted executables with structured parameters.
- **SHELL_COMMAND**: invoke allowlisted shell commands as a raw string (disabled by default).

## Running the service

```bash
mvn spring-boot:run
```

The server starts on `http://localhost:8080` by default.

## Testing

```bash
mvn test
```

## Example request (EXECUTABLE mode)

```bash
curl -X POST http://localhost:8080/api/commands/execute \
  -H 'Content-Type: application/json' \
  -d '{
    "commandMode": "EXECUTABLE",
    "executable": "/usr/bin/ls",
    "args": ["-l"],
    "parameters": {"--color": "auto"},
    "ruleContext": {
      "jobAttributes": {"jobId": "123"},
      "tradeAttributes": {"tradeId": "T-9"},
      "productAttributes": {"productId": "P-3"}
    }
  }'
```

## Example request (SHELL_COMMAND mode)

Enable shell execution and allowlisted shell commands in `application.yml` before running:

```yaml
command:
  policy:
    allow-shell-execution: true
    allowed-shell-commands:
      - /usr/bin/echo
```

Request:

```bash
curl -X POST http://localhost:8080/api/commands/execute \
  -H 'Content-Type: application/json' \
  -d '{
    "commandMode": "SHELL_COMMAND",
    "rawCommand": "/usr/bin/echo ${trade.tradeId}",
    "ruleContext": {
      "tradeAttributes": {"tradeId": "T-100"}
    }
  }'
```

## Policy configuration

Edit `src/main/resources/application.yml` and set `command.policy.allowed-executables` and other
policy fields to match your environment. Shell execution and sudo are disabled by default.

## Security notes

- Commands are allowlisted and validated against denied tokens.
- Shell execution is opt-in and requires explicit allowlisting of shell commands.
- Use `ruleContext` to substitute dynamic values in parameters or raw commands via `${job.*}`,
  `${trade.*}`, or `${product.*}` tokens.

