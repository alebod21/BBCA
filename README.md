# BBCA
Chat Client Project for Computer Science Senior Capstone

Run ChatServerRunner to create a chat server

Run ChatClientRunner to make a client

	Give the server's IP and port to connect to the server
	
	The server will request a username. Valid usernames are alphanumeric. If a user has been banned with a certain username
	since the server started running, that name is invalid as well.
	
	Other users on that server will see what you type, and you'll see what they type.
	
	commands:
	
	/ban [username] to start a vote to ban that user. 
	
	/y or /n to confirm or deny a ban. If more than half of the members of the server vote yes, the user will be banned

	@[username] [message] to send a private message to another user