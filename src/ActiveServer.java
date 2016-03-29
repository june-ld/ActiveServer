import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class ActiveServer {
	
	private ServerSocket server;
    private Socket connection;
    
    private static boolean testing = false; // for testing: set true and use testing statement in main()

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ActiveServer run = new ActiveServer();
		
		// for testing
		if (testing){
			String test[] = run.getUserOrgList("2");
			System.out.println(test.length);
			for(int i = 0; i < test.length; i++)
				System.out.println(test[i]);
			return;
		}
		
		// set the connection
		if(!run.setConnection()){
            System.out.println("Cannot bind to port 4444. Please try again later");
            return;
        } else
            System.out.println("Now listening on port 4444");
		
		// main loop to run server indefinitely
		int i = 0;
		
		Calendar check = run.setStartTime();
		run.checkChallenges();
		
        while(true){
        	// stop the server after i requests
        	if(i == 20)
        		break;
        	
        	// check the challenges every 30 minutes
        	if(Calendar.getInstance().after(check)){
        		run.checkChallenges();
        		check.add(Calendar.MINUTE, 1);
        		check = run.setStartTime();
        	}
        	
        	
        	// start listening for requests
            run.runServer();
            i++;
        }
        
        run.closeConnection();
	}
	
	public Calendar setStartTime(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.ENGLISH);
		
		Calendar now = Calendar.getInstance();
		Calendar temp = Calendar.getInstance();
		temp.set(Calendar.MINUTE, 30);
		
		if(now.before(temp)){
			System.out.print("Start check at: ");
			System.out.println(dateFormat.format(temp.getTime()));
			return temp;
		}
		else{
			now.set(Calendar.MINUTE, 0);
			now.add(Calendar.HOUR_OF_DAY, 1);
			System.out.print("Start check at: ");
			System.out.println(dateFormat.format(now.getTime()));
			return now;
		}
	}
	
	/**********************************************
	 * 	closeConnection()
	 *	closes serverSocket bind.
	 **********************************************/
    public void closeConnection(){
        try {
            server.close();		// close server socket
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**********************************************
	 * 	setConnection()
	 *	binds socket to port 4444
	 *	returns true if successful
	 **********************************************/
    public boolean setConnection(){
        try {
            server = new ServerSocket(4444);	//initialize server socket
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
	
    /**********************************************
	 * 	runServer()
	 *	Main server function
	 *	Initializes in and out streams.
	 *	reads input from socket
	 *	responds to requests
	 *	closes connections
	 **********************************************/
    public void runServer(){
        System.out.println("Waiting for connection...");
		
		// try catch for socket, in, and out exceptions
        try {
			// accept connection
            connection = server.accept();
            System.out.println("Connection received from " + connection.getInetAddress().getHostName());

			// initialize input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());

			// receive request from socket
            String msgFromClient = "";
            msgFromClient = in.readLine();

			// if no message, return
            if(msgFromClient.isEmpty())
                return;
			
			// print request message to stdout
            System.out.println("************** request ****************");
			System.out.println(msgFromClient);

			// tokenize request
            String[] request = msgFromClient.split(",");
            
            // response string
            String response;

			// if it is a GET request, run handleGET
            if(request[0].matches("getUser")){
				response = getUser(request[1],request[2]);
            }
            else if(request[0].matches("addUser")){
            	response = addUser(request[1],request[2], request[3], request[4], request[5]);
            }
            else if (request[0].matches("getAllChallenges")){
            	String[] result = getAllChallenges();
            	String size = String.format("%d\n", result.length);
            	System.out.println("*************** reply *****************");
            	out.write(size.getBytes());
            	for(int i = 0; i < result.length; i++){
            		System.out.println(result[i]);
            		String sendout = result[i]+"\r\n";
            		out.write(sendout.getBytes());
            	}
            	response = "\r\n";
            }
            else if (request[0].matches("addChallenge")){
            	response = addChallenge(msgFromClient.substring(13));
            }
            else if (request[0].matches("updateUserEXP")){
            	response = updateUserEXP(request[1], request[2]);
            }
            else if (request[0].matches("addComment")){
            	response = addComment(request[1], request[2], request[3]);
            }
            else if (request[0].matches("getComments")){
            	String[] result = getComments(request[1]);
            	String size = String.format("%d\n", result.length);
            	System.out.println("*************** reply *****************");
            	out.write(size.getBytes());
            	for(int i = 0; i < result.length; i++){
            		System.out.println(result[i]);
            		String sendout = result[i]+"\r\n";
            		out.write(sendout.getBytes());
            	}
            	response = "\r\n";
            }
            else if (request[0].matches("checkIn")){
            	response = checkIn(request[1], request[2]);
            }
            else if (request[0].matches("chInStatus")){
            	response = checkInStatus(request[1], request[2]);
            }
            else if (request[0].matches("chCount")){
            	response = checkInCount(request[1]);
            }
            else if (request[0].matches("adOrg")){
            	response = addOrg(request[1], request[2]);
            }
            else if (request[0].matches("adU2Org")){
            	response = addUserToOrg(request[1], request[2]);
            }
            else if (request[0].matches("UOList")){
            	String[] result = getUserOrgList(request[1]);
            	String size = String.format("%d\n", result.length);
            	System.out.println("*************** reply *****************");
            	out.write(size.getBytes());
            	for(int i = 0; i < result.length; i++){
            		System.out.println(result[i]);
            		String sendout = result[i]+"\r\n";
            		out.write(sendout.getBytes());
            	}
            	response = "\r\n";
            }
            else if (request[0].matches("hist")){
            	String[] result = getHistory(request[1]);
            	String size = String.format("%d\n", result.length);
            	System.out.println("*************** reply *****************");
            	out.write(size.getBytes());
            	for(int i = 0; i < result.length; i++){
            		System.out.println(result[i]);
            		String sendout = result[i]+"\r\n";
            		out.write(sendout.getBytes());
            	}
            	response = "\r\n";
            }
            else if (request[0].matches("rmCH")){
            	response = removeChallenge(request[1]);
            }
            else
            	response = "BAD_REQUEST\n";
            
            
            if(response.matches("\r\n")){
            	connection.close();
            	return;
            }
            	
			
            System.out.println("*************** reply *****************");
            System.out.print(response);
            out.write(response.getBytes());
            
			// once response is sent, close the connection
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**********************************************
	 * 	getUser()
	 *	queries database for user
	 **********************************************/
	private String getUser(String username, String password) {
		// TODO Auto-generated method stub
		ActiveUser user;
		System.out.println("Query User");
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "getUser", username);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			String query = sqlIn.readLine();	// readline from input
			
			// if query is empty return NOTFOUND
			if(query.isEmpty())
				return "NOTFOUND\n";
			else if(query.matches("None"))
				return "NOTFOUND\n";
			
			// print query, split fields retrieved and construct user
			System.out.print(query + "\n");
			String[] response = query.split(",");
            user = new ActiveUser(Integer.valueOf(response[0]), response[1], response[2], response[3], response[4], Integer.valueOf(response[5]));
			
            // compare user password with input, return query if successful
            if(password.matches(user.getPassword())){
            	System.out.println("match");
            	return query +"\n";
            }
            else
            	return "NOTFOUND\n";
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "NOTFOUND\n";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "NOTFOUND\n";
		}
	}
	
	/**********************************************
	 * 	addUser()
	 *	adds user for database
	 **********************************************/
	private String addUser(String username, String password, String email, String secAns, String exp){
		System.out.println("Add User");
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "addUser", username, password, email, secAns, exp);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			String query = sqlIn.readLine();	// readline from input
			
			// if query is empty return FAIL
			if(query.isEmpty())
				return "ADD_FAILED\n";
			
			// if query exists return EXISTS
			if(query.matches("USER_EXISTS"))
				return "ADD_EXISTS";
			
			// return query
			return query +"\n";
			
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ADD_FAILED\n";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ADD_FAILED\n";
		}
	}
	
	/**********************************************
	 * 	updateUserEXP()
	 *	updates given user with given EXP
	 **********************************************/
	public String updateUserEXP(String id, String exp){
		System.out.println("Update User");
		System.out.println(String.format("%s and %s", id, exp));
		
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "updateUserEXP", id, exp);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			String query = sqlIn.readLine();	// readline from input
			
			// if query is empty return FAIL
			if(query == null)
				return "UPDATE_FAILED\n";
			if(query.isEmpty())
				return "UPDATE_FAILED\n";
			
			// return query
			return query +"\n";
			
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "UPDATE_FAILED\n";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "UPDATE_FAILED\n";
		}
	}
	
	/**********************************************
	 * 	getAllChallenges()
	 *	retrieves all challenges from database
	 **********************************************/
	public String[] getAllChallenges(){
		System.out.println("Get Challenges");
		String[] result;
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "getAllChallenges");
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			
			// get number of challenges
			String queryItem = sqlIn.readLine();
			int arraySize = Integer.valueOf(queryItem);
			if(queryItem.isEmpty())
				return null;
			else
				result = new String[arraySize];
			
			for(int i = 0; i < arraySize; i++){
				queryItem = sqlIn.readLine();
				if(queryItem.isEmpty())
					return null;
				else
					result[i] = queryItem;
			}
			
			return result;
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**********************************************
	 * 	addChallenge()
	 *	adds a new challenge to the database
	 **********************************************/
	public String addChallenge(String parseString){
		System.out.println("Add Challenge");
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "addChallenge", parseString);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			String query = ""; 
			query = sqlIn.readLine();	// readline from input
			
			// if query is empty return FAIL
			if(query.isEmpty())
				return "ADD_FAILED\n";
			
			// return query
			return query +"\n";
			
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ADD_FAILED\n";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ADD_FAILED\n";
		}
	}
	
	/**********************************************
	 * 	addComment()
	 *	adds a new comment to the database
	 **********************************************/
	public String addComment(String challengeID, String userID, String text){
		System.out.println("Add Comment");
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "addComment", challengeID, userID, text);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			String query = ""; 
			query = sqlIn.readLine();	// readline from input
			
			// if query is empty return FAIL
			if(query.isEmpty())
				return "ADD_FAILED\n";
			
			// return query
			return query +"\n";
			
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ADD_FAILED\n";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ADD_FAILED\n";
		}
	}
	
	public String[] getComments(String challengeID){
		System.out.println("Get Comments");
		String[] result;
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "getComments", challengeID);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			
			// get number of challenges
			String queryItem = sqlIn.readLine();
			int arraySize = Integer.valueOf(queryItem);
			if(queryItem.isEmpty())
				return null;
			else
				result = new String[arraySize];
			
			for(int i = 0; i < arraySize; i++){
				queryItem = sqlIn.readLine();
				if(queryItem.isEmpty())
					return null;
				else
					result[i] = queryItem;
			}
			
			return result;
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String checkIn(String userID, String challengeID){
		System.out.println("Check In");
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "checkIn", userID, challengeID);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			String query = ""; 
			query = sqlIn.readLine();	// readline from input
			
			// if query is empty return FAIL
			if(query.isEmpty())
				return "FAILED\n";
			
			// return query
			return query +"\n";
			
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FAILED\n";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FAILED\n";
		}
	}
	
	public String checkInStatus(String userID, String challengeID){
		System.out.println("Check In Status");
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "chInStatus", userID, challengeID);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			String query = ""; 
			query = sqlIn.readLine();	// readline from input
			
			// if query is empty return FAIL
			if(query.isEmpty())
				return "FAILED\n";
			
			// return query
			return query +"\n";
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FAILED\n";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FAILED\n";
		}
	}
	
	public String checkInCount(String challengeID){
		System.out.println("Check In Count");
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "chCount", challengeID);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			String query = ""; 
			query = sqlIn.readLine();	// readline from input
			
			// if query is empty return FAIL
			if(query.isEmpty())
				return "FAILED\n";
			
			// return query
			return query +"\n";
			
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FAILED\n";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FAILED\n";
		}
	}
	
	public String addOrg(String orgName, String userID){
		System.out.println("Add New Organization");
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "adOrg", orgName, userID);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			String query = ""; 
			query = sqlIn.readLine();	// readLine from input
			
			// if query is empty return FAIL
			if(query.isEmpty())
				return "ADD_FAILED\n";
			
			// return query
			return query +"\n";
			
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ADD_FAILED\n";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ADD_FAILED\n";
		}
	}
	
	public String addUserToOrg(String code, String userID){
		System.out.println("Add User to Organization");
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "adU2Org", code, userID);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			String query = ""; 
			query = sqlIn.readLine();	// readLine from input
			
			// if query is empty return FAIL
			if(query.isEmpty())
				return "ADD_FAILED\n";
			
			// return query
			return query +"\n";
			
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ADD_FAILED\n";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ADD_FAILED\n";
		}
	}
	
	public String[] getUserOrgList(String userID){
		String[] result;
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "UOList", userID);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			
			// get number of challenges
			String queryItem = sqlIn.readLine();
			int arraySize = Integer.valueOf(queryItem);
			if(queryItem.isEmpty())
				return null;
			else
				result = new String[arraySize];
			
			for(int i = 0; i < arraySize; i++){
				queryItem = sqlIn.readLine();
				if(queryItem.isEmpty())
					return null;
				else
					result[i] = queryItem;
			}
			
			return result;
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String[] getHistory(String userID){
		String[] result;
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "hist", userID);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			
			// get number of challenges
			String queryItem = sqlIn.readLine();
			int arraySize = Integer.valueOf(queryItem);
			if(queryItem.isEmpty() || queryItem.matches("None"))
				return null;
			else
				result = new String[arraySize];
			
			for(int i = 0; i < arraySize; i++){
				queryItem = sqlIn.readLine();
				if(queryItem.isEmpty())
					return null;
				else
					result[i] = queryItem;
			}
			
			return result;
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String removeChallenge(String challengeID){
		System.out.println("Remove Challenge");
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "rmCH", challengeID);
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			String query = ""; 
			query = sqlIn.readLine();	// readLine from input
			
			// if query is empty return FAIL
			if(query == null)
				return "FAILED\n";
			if(query.isEmpty())
				return "FAILED\n";
			
			// return query
			return query +"\n";
			
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FAILED\n";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FAILED\n";
		}
	}
	
	public void checkChallenges(){
		System.out.println("Checking challenges");
		
		String[] result;
		
		// build process and try/catch
		ProcessBuilder pb = new ProcessBuilder("python", "SQLScripts.py", "check");
        try {
        	// start process and get input stream
			Process p = pb.start();
			BufferedReader sqlIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			p.waitFor();	// wait for process to finish
			
			// get number of challenges
			String queryItem = sqlIn.readLine();
			int arraySize = Integer.valueOf(queryItem);
			if(queryItem.isEmpty() || queryItem.matches("None"))
				return;
			else
				result = new String[arraySize];
			
			for(int i = 0; i < arraySize; i++){
				queryItem = sqlIn.readLine();
				if(queryItem.isEmpty())
					return;
				else
					result[i] = queryItem;
			}
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.ENGLISH);
			Calendar now = Calendar.getInstance();
			for(int i = 0; i < result.length; i++){
				
				String challenges[] = result[i].split(","); 
				Calendar time = Calendar.getInstance();
				
				try {
					time.setTime(dateFormat.parse(challenges[1]));
					if(now.after(time)){
						String num = challenges[0];
						System.out.println(String.format("Removing challenge: %s", num ));
						removeChallenge(num);
					}
				} catch (ParseException e) {
					continue;
				}
			}	
			return;
            
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
}
