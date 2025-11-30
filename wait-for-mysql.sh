#!/bin/sh
# wait-for-mysql.sh: Waits for the MySQL host to be ready on a specific port.

# Variables:
# $1: Hostname (e.g., 'db' in a Docker Compose network)
# $2: Port (e.g., '3306')
HOST="$1"
PORT="$2"

# FIX: Check for the WAIT_TIMEOUT environment variable and use it, 
# otherwise default to 180 seconds (using standard shell parameter expansion).
TIMEOUT=${WAIT_TIMEOUT:-180}

SLEEP=1
COUNT=0

echo "Waiting for $HOST:$PORT to be available (Timeout: $TIMEOUT seconds)..."

# The loop attempts to connect using netcat (nc) or /dev/tcp
# We use a generic approach for maximum compatibility.

# Check if 'nc' (netcat) is available
if command -v nc >/dev/null 2>&1; then
    while ! nc -z "$HOST" "$PORT" >/dev/null 2>&1; do
        if [ "$COUNT" -ge "$TIMEOUT" ]; then
            echo "Error: Timeout reached ($TIMEOUT seconds) while waiting for $HOST:$PORT."
            exit 1
        fi
        sleep "$SLEEP"
        COUNT=$((COUNT + SLEEP))
        echo "Still waiting ($COUNT/$TIMEOUT seconds)..."
    done
# Fallback for systems without netcat (e.g., some minimal Alpine images)
elif [ -e /dev/tcp ]; then
    # Use Bash's built-in TCP connection feature
    while ! exec 6<>/dev/tcp/"$HOST"/"$PORT"; do
        if [ "$COUNT" -ge "$TIMEOUT" ]; then
            echo "Error: Timeout reached ($TIMEOUT seconds) while waiting for $HOST:$PORT."
            exit 1
        fi
        sleep "$SLEEP"
        COUNT=$((COUNT + SLEEP))
        echo "Still waiting ($COUNT/$TIMEOUT seconds)..."
    done
else
    echo "Warning: Neither 'nc' nor '/dev/tcp' found. Sleeping for 15 seconds to give DB time to start."
    sleep 15
fi

echo "$HOST:$PORT is up! Starting application."

# End of script
