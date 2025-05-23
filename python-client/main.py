# Generated by GPT-4o
import websocket
import threading
import time
import json

# WebSocket URL
WS_URL = "ws://localhost:8080/connect/chat"

# Function to handle incoming messages
def on_message(ws, message):
    print("Received message:", message)

# Function to handle errors
def on_error(ws, error):
    print("Error:", error)

# Function to handle WebSocket closure
def on_close(ws, close_status_code, close_msg):
    print("WebSocket closed:", close_status_code, close_msg)

# Function to handle connection and subscription
def on_open(ws):
    print("WebSocket connection opened.")

    connect_frame = "CONNECT\naccept-version:1.2\n\n\0"
    ws.send(connect_frame)
    print("Sent STOMP CONNECT frame.")

    subscribe_frame = "SUBSCRIBE\ndestination:/subscribe/chat/messages/user1\nid:user1\nack:auto\n\n\0"
    ws.send(subscribe_frame)
    print("Subscribed to /subscribe/chat/messages/1")

    # Send a test message after subscribing
    time.sleep(1)

# Function to send a message to the server
def send_message(ws):
    # STOMP SEND frame
    message_payload = {
        "roomId": 1,
        "sender": "python_client",
        "content": "Hello from Python!"
    }
    send_frame = f"SEND\ndestination:/publish/chat/message\ncontent-type:application/json\n\n{json.dumps(message_payload)}\0"
    ws.send(send_frame)
    print("Message sent:", message_payload)

# Start the WebSocket connection
if __name__ == "__main__":
    # Create a WebSocket connection
    ws = websocket.WebSocketApp(
        WS_URL,
        on_message=on_message,
        on_error=on_error,
        on_close=on_close,
        on_open=on_open,
    )

    # Run the WebSocket in a separate thread
    wst = threading.Thread(target=ws.run_forever)
    wst.daemon = True
    wst.start()

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        ws.close()
        print("WebSocket connection closed.")
