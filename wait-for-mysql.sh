#!/bin/sh
# wait-for-mysql.sh: Waits for the MySQL host to be ready on a specific port, then execs the remaining args.
# Usage: ./wait-for-mysql.sh <host> <port> [extra args...]
# Env: WAIT_TIMEOUT (seconds, default 180)

HOST="$1"
PORT="$2"
shift 2  # Consume HOST and PORT; remaining "$@" are for the app (e.g., java args)

# Default timeout
TIMEOUT=${WAIT_TIMEOUT:-180}
SLEEP_INTERVAL=1
COUNT=0

echo "Waiting for $HOST:$PORT to be available (Timeout: $TIMEOUT seconds)..."

# Check if 'nc' (netcat) is available
if command -v nc >/dev/null 2>&1; then
    while ! nc -z -w1 "$HOST" "$PORT" >/dev/null 2>&1; do
        if [ "$COUNT" -ge "$TIMEOUT" ]; then
            echo "Error: Timeout reached ($TIMEOUT seconds) while waiting for $HOST:$PORT."
            exit 1
        fi
        sleep "$SLEEP_INTERVAL"
        COUNT=$((COUNT + SLEEP_INTERVAL))
        echo "Still waiting ($COUNT/$TIMEOUT seconds)..."
    done
# Fallback for systems with Bash's /dev/tcp
elif command -v bash >/dev/null 2>&1; then
    while ! bash -c "exec 6<>/dev/tcp/$HOST/$PORT" 2>/dev/null; do
        if [ "$COUNT" -ge "$TIMEOUT" ]; then
            echo "Error: Timeout reached ($TIMEOUT seconds) while waiting for $HOST:$PORT."
            exit 1
        fi
        sleep "$SLEEP_INTERVAL"
        COUNT=$((COUNT + SLEEP_INTERVAL))
        echo "Still waiting ($COUNT/$TIMEOUT seconds)..."
    done
    # Close the FD if connected
    exec 6>&-
else
    echo "Warning: Neither 'nc' nor Bash '/dev/tcp' found. Sleeping for 15 seconds to give DB time to start."
    sleep 15
fi

echo "$HOST:$PORT is up! Starting application."
# Exec the remaining args (e.g., java command) to replace this script's PID
exec "$@"
