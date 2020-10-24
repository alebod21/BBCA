# BBCA
Chat Client Project for Computer Science Senior Capstone

Run ChatServerRunner to create a chat server

Run ChatClientRunner to make a client

	Give the server's IP and port to connect to the server
	Hit the return key on the text client, and click "send" on the gui client to send your input
	
	The server will request a username. Valid usernames are alphanumeric. If a user has been banned with a certain username
	since the server started running, that name is invalid as well.
	
	Other users on that server will see what you type, and you'll see what they type.
	
	BANNING
		On the text client, type /ban [username] to start a vote to ban that user. 
		On the gui client, select a user from the drop down menu in the top right, and click "Ban" to start a vote.
		Type /y or /n to confirm or deny a ban. If more than half of the members of the server vote yes, the user will be banned.


	PRIVATE CHAT
		type @[username] [message] to send a private message to another user
		o chat privately to multiple users, use @[username] @[username] @[username]... [message]
	
	SEE CURRENT USERS
		On the gui client, a list of users is automatically provided on the right side of the window
		on the text client, type /whoishere to receive a list of users
	
	QUIT
		type /quit to quit