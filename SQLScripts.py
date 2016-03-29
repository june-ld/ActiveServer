import sys                  # import arguments
import sqlite3              # Import sqlite3 module
import random

##### getUser #####
def getUser( requestedUser ):
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()
    command = "SELECT * FROM users WHERE username= ? ;"
    cur.execute(command, (requestedUser,))

    queryResult = cur.fetchone()    # returns None if not found

    if queryResult is None:
        print "None"
    else:
        sendback = "{0},{1},{2},{3},{4},{5}".format(queryResult[0],queryResult[1],queryResult[2],queryResult[3],
                                                    queryResult[4],queryResult[5])
        print sendback

    conn.close()
    return

##### addUser #####
def addUser( username, password, email, secAns, currExp ):

    # establish connection to db and look for given username
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()
    command = "SELECT * FROM users WHERE username='{0}';".format(username)
    cur.execute(command)
    queryResult = cur.fetchone()    # returns None if not found

    # if username exists alert client
    if queryResult is not None:
        print "USER_EXISTS"
        return

    # insert user into database
    command = "INSERT INTO {0} ({1},{2},{3},{4},{5}) VALUES(?,?,?,?,?);".format("users","username","password","email_addr","security_ans","currentEXP")
                                                                                                   
    cur.execute(command, (username,password,email,secAns,currExp) )
    conn.commit()

    # retrieve values of user
    command = "SELECT * FROM users WHERE username='{0}';".format(username)
    cur.execute(command)
    queryResult = cur.fetchone()

    # send query to output
    sendback = "{0},{1},{2},{3},{4},{5}".format(queryResult[0],queryResult[1],queryResult[2],queryResult[3],
                                                    queryResult[4],queryResult[5])
    print sendback

    # close connection
    conn.close()
    return

##### updateUserEXP #####
def updateUserEXP( userID, value ):

    # establish connection to db and look for given username
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()

    command = "UPDATE users SET currentEXP = {0} WHERE id = {1};".format(value, userID);
    cur.execute(command)
    conn.commit()

    # retrieve values of user
    command = "SELECT * FROM users WHERE id= {0};".format(userID)
    cur.execute(command)
    queryResult = cur.fetchone()

    # send query to output
    sendback = "{0},{1},{2},{3},{4},{5}".format(queryResult[0],queryResult[1],queryResult[2],queryResult[3],
                                                    queryResult[4],queryResult[5])
    print sendback

    # close connection
    conn.close()
    return

    

##### getAllChallenges #####
def getAllChallenges():
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()

    # print total number of challenges in table 
    cur.execute("SELECT count(*) FROM challenges WHERE active = 1;")
    queryResult = cur.fetchone()
    print(queryResult[0])

    # get all challenges from table
    command = "SELECT challenge_id,challenge_name,challenge_date_event,challenge_date_created,challenge_location,"\
              "username,challenge_host_isorg,challenge_details,challenge_exp_value,challenge_upvotes,"\
              "challenge_checkin_code,org_name,challenge_end_time FROM users,challenges,organizations"\
              " WHERE users.id = challenges.challenge_host_id AND challenges.challenge_org_id = organizations.org_id AND active = 1;"
    cur.execute(command)

    # print each challenge to output
    queryResult = cur.fetchall()    # returns None if not found
    if queryResult is None:
        print "None"
    else:
        for item in queryResult:
            print "{0},{1},{2},{3},{4},{5},{6},{7},{8},{9},{10},{11},{12}".format(item[0],item[1],item[2],item[3],
                                                                        item[4],item[5],item[6],item[7],
                                                                        item[8],item[9],item[10],item[11],item[12])
        print # extra line to indicate end

    # close databse connection
    conn.close()
    return;

##### getUserHistory #####
def getUserHistory(userID):
    
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()

    # print total number of challenges in table
    command = "SELECT count(*) FROM challenges,checkedIn,users,organizations WHERE challenge_id = checkin_ch_id AND challenge_host_id = id AND challenge_org_id = org_id AND checkin_user_id = {0};".format(userID)
    cur.execute(command)
    queryResult = cur.fetchone()
    print(queryResult[0])

    # get all challenges from table
    command = "SELECT challenge_name,challenge_host_isorg,username,org_name FROM challenges,checkedIn,users,organizations WHERE challenge_id = checkin_ch_id AND challenge_host_id = id AND challenge_org_id = org_id AND checkin_user_id = {0};".format(userID)
    cur.execute(command)

    # print each challenge to output
    queryResult = cur.fetchall()    # returns None if not found
    if queryResult is None:
        print "None"
    else:
        for item in queryResult:
            print "{0},{1},{2},{3}".format(item[0],item[1],item[2],item[3])
        print # extra line to indicate end

    # close databse connection
    conn.close()
    return;

##### addChallenge #####
# DO NOT SEND CHALLENGE ID # DO NOT SEND NAME, USE USER ID!#
def addChallenge( parseString ):

    # tokenize data from client
    tok = parseString.split(',')

    # build statement to insert challenge
    command = "INSERT INTO challenges (challenge_name,challenge_date_event,challenge_date_created,challenge_location,challenge_host_id,challenge_host_isorg,challenge_details,challenge_exp_value,challenge_upvotes,challenge_checkin_code,challenge_org_id, challenge_end_time) VALUES " \
              "(?,?,?,?,?,?,?,?,?,?,?,?);"

    #establish connection to database and execute insert
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()
    cur.execute(command, (tok[0],tok[1],tok[2],tok[3],tok[4],tok[5],tok[6],tok[7],tok[8],tok[9],tok[10],tok[11]) )
    conn.commit()

    # retrieve values of last added challenge
    lastid = cur.lastrowid
    command = "SELECT challenge_id,challenge_name,challenge_date_event,challenge_date_created,challenge_location,"\
              "username,challenge_host_isorg,challenge_details,challenge_exp_value,challenge_upvotes,"\
              "challenge_checkin_code,org_name,challenge_end_time FROM users,challenges,organizations"\
              " WHERE users.id = challenges.challenge_host_id AND challenges.challenge_org_id = organizations.org_id AND challenge_id = {0};".format(lastid)
    cur.execute(command)
    queryResult = cur.fetchone()

    if queryResult == None:
        print "error"
        conn.close();
        return

    # print output of query to send to client
    print "{0},{1},{2},{3},{4},{5},{6},{7},{8},{9},{10},{11},{12}".format(queryResult[0],queryResult[1],queryResult[2],queryResult[3],
                                                                        queryResult[4],queryResult[5],queryResult[6],queryResult[7],
                                                                        queryResult[8],queryResult[9],queryResult[10],queryResult[11],queryResult[12])
    #close the database connection
    conn.close()
    return

############# addComment #####################
def addComment(challengeID, userID, text):

    # build statement to insert comment
    command = "INSERT INTO comments (comment_challenge_id, comment_host_id, comment_text) VALUES (?,?,?);"

    #establish connection to database and execute insert
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()
    cur.execute(command, (challengeID, userID, text))
    conn.commit()

    # retrieve values of last added comment
    lastid = cur.lastrowid
    command = "SELECT comment_id,comment_text,username FROM comments,users WHERE comment_id = {0} AND comment_host_id = id;".format(lastid)
    cur.execute(command)
    queryResult = cur.fetchone()

    if queryResult == None:
        print "error"
        conn.close();
        return

    # print output of query to send to client
    print "{0},{1},{2}".format(queryResult[0],queryResult[1],queryResult[2])
    #close the database connection
    conn.close()
    return

############# getComments ##############
def getComments(challengeID):
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()

    # print total number of comments with matching challengeID in table
    command = "SELECT count(*) FROM comments WHERE comment_challenge_id = {0};".format(challengeID)
    cur.execute(command)
    queryResult = cur.fetchone()
    print(queryResult[0])

    # get all comments from table with matching challengeID
    command = "SELECT comment_id,comment_text,username FROM comments,users WHERE comment_challenge_id = {0} AND comment_host_id = id;".format(challengeID)
    cur.execute(command)

    # print each comment to output
    queryResult = cur.fetchall()    # returns None if not found
    if queryResult is None:
        print "None"
    else:
        for item in queryResult:
            print "{0},{1},{2}".format(item[0],item[1],item[2])
        print # extra line to indicate end

    # close databse connection
    conn.close()
    return

############# checkIn ##############
def checkIn ( userID, challengeID):
    # build statement to insert comment
    command = "INSERT INTO checkedIn (checkin_user_id,checkin_ch_id) VALUES({0},{1});".format(userID, challengeID)

    #establish connection to database and execute insert
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()
    cur.execute(command)
    conn.commit()

    # retrieve values of last added comment
    lastid = cur.lastrowid
    command = "SELECT * FROM checkedIn WHERE checkin_id = {0};".format(lastid)
    cur.execute(command)
    queryResult = cur.fetchone()

    if queryResult == None:
        print "error"
        conn.close();
        return

    # print output of query to send to client
    print "SUCCESS"
    #close the database connection
    conn.close()
    return

############# checkInStatus ##############
def checkInStatus(userID, challengeID):
    # build statement to insert comment
    command = "SELECT count(*) FROM checkedIn WHERE checkin_ch_id = {1} AND checkin_user_id = {0};".format(userID, challengeID)

    #establish connection to database and execute insert
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()
    cur.execute(command)

    # retrieve value and send to output
    queryResult = cur.fetchone()
    print(queryResult[0])

    # close connection
    conn.close()
    return

############# checkInStatus ##############
def getCheckInCount(challengeID):
    # build statement to insert comment
    command = "SELECT count(*) FROM checkedIn WHERE checkin_ch_id = {0};".format(challengeID)

    #establish connection to database and execute insert
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()
    cur.execute(command)

    # retrieve value and send to output
    queryResult = cur.fetchone()
    print(queryResult[0])

    # close connection
    conn.close()
    return

############# addNewOrg ######################
def addNewOrg( orgName, userID):

    #establish connection to database
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()

    # check if organization already exists
    command = "SELECT count(*) FROM organizations WHERE  org_name = ? ;"
    cur.execute(command, (orgName,) )
    queryResult = cur.fetchone()
    if ( int(queryResult[0]) > 0 ):
        print "AE"
        conn.close()
        return

    # write insert to file to inspect later
    with open('toAdd.txt', 'a') as f:
        command = "{0},{1}\n".format(orgName, userID)
        f.write(command)

    
    # THIS CODE ADDS A NEW ORG
    ''' 
    # generate a random code not already in use
    code = 0;
    while(True):
        code = random.randrange(100000,999999,1)
        command = "SELECT count(*) FROM organizations WHERE  org_passcode = {0};".format(code)
        cur.execute(command)
        queryResult = cur.fetchone()
        if ( int(queryResult[0]) == 0):
            break

    # build insert statement and execute
    command = "INSERT INTO organizations (org_name,org_passcode) VALUES ('{0}', {1});".format( orgName, code)
    cur.execute(command)
    conn.commit()

    # retrieve values of last added organizations
    lastid = cur.lastrowid
    command = "SELECT * FROM organizations WHERE org_id = {0}; ".format(lastid)
    cur.execute(command)
    queryResult = cur.fetchone()

    # print output of query to send to client
    print "{0},{1},{2}".format(queryResult[0],queryResult[1],queryResult[2])

    # insert userID into approved users and execute
    command = "INSERT INTO approved_users (approved_org_id, approved_user_id) VALUES ({0},{1});".format( lastid, userID)
    cur.execute(command)
    conn.commit()
    '''

    #close the database connection
    print "SUCCESS"
    conn.close()
    return

############# addOrgFinal ########################
# THIS CODE ADDS A NEW ORG

def addOrgFinal( orgName, userID ):

    #establish connection to database
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()
    
    # generate a random code not already in use
    code = 0;
    while(True):
        code = random.randrange(100000,999999,1)
        command = "SELECT count(*) FROM organizations WHERE  org_passcode = {0};".format(code)
        cur.execute(command)
        queryResult = cur.fetchone()
        if ( int(queryResult[0]) == 0):
            break

    # build insert statement and execute
    command = "INSERT INTO organizations (org_name,org_passcode) VALUES ('{0}', {1});".format( orgName, code)
    cur.execute(command)
    conn.commit()

    # retrieve values of last added organizations
    lastid = cur.lastrowid
    command = "SELECT * FROM organizations WHERE org_id = {0}; ".format(lastid)
    cur.execute(command)
    queryResult = cur.fetchone()

    # insert userID into approved users and execute
    command = "INSERT INTO approved_users (approved_org_id, approved_user_id) VALUES ({0},{1});".format( lastid, userID)
    cur.execute(command)
    conn.commit()

    #close the database connection
    print "SUCCESS"
    conn.close()
    return

############# addUserToOrg ######################
def addUserToOrg(code, userID):

    # statement to check for the code
    command = "SELECT org_id FROM organizations WHERE org_passcode = {0};".format(code)

    # establish connection to database
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()
    cur.execute(command)

    # check if code does not match any existing org code
    queryResult = cur.fetchone()
    if ( queryResult == None):
        print "DNE"
        conn.close()
        return

    # check if user is already approved
    orgID = int(queryResult[0])
    command = "SELECT count(*) FROM approved_users WHERE approved_org_id = {0} AND approved_user_id = {1};".format(orgID, userID)
    cur.execute(command)
    queryResult = cur.fetchone()
    if ( int(queryResult[0]) > 0 ):
        print "AE"
        conn.close()
        return
        
    # insert userID into approved users and execute
    command = "INSERT INTO approved_users (approved_org_id, approved_user_id) VALUES ({0},{1});".format(orgID, userID)
    cur.execute(command)
    conn.commit()
    print "SUCCESS"
    
    #close the database connection
    conn.close()
    return

############# getUserOrgList ######################
def getUserOrgList(userID):

    # establish connection to database
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()
    
    # print total number of comments with matching challengeID in table
    command = "SELECT count(*) FROM approved_users,users,organizations WHERE approved_org_id = org_id AND approved_user_id = id AND approved_user_id = {0};".format(userID)
    cur.execute(command)
    queryResult = cur.fetchone()
    print(queryResult[0])

    # get all comments from table with matching challengeID
    command = "SELECT org_id,org_name,org_passcode FROM approved_users,users,organizations WHERE approved_org_id = org_id AND approved_user_id = id AND approved_user_id = {0};".format(userID)
    cur.execute(command)

    # print each comment to output
    queryResult = cur.fetchall()    # returns None if not found
    if queryResult is None:
        print "None"
    else:
        for item in queryResult:
            print "{0},{1},{2}".format(item[0],item[1],item[2])
        print # extra line to indicate end

    # close databse connection
    conn.close()
    return

######### removeChallenge #################
def removeChallenge(challengeID):
    # establish connection to database
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()

    command = "UPDATE challenges SET active = 0 WHERE challenge_id = {0};".format(challengeID)
    cur.execute(command)
    conn.commit()

    print "SUCCESS"
    conn.close()
    return

############# checkTimes ######################
def checkChTimes():
    # establish connection to database
    conn = sqlite3.connect('active.db')
    cur = conn.cursor()
    
    # print total number of comments with matching challengeID in table
    command = "SELECT count(*) FROM challenges WHERE active = 1;"
    cur.execute(command)
    queryResult = cur.fetchone()
    print(queryResult[0])

    # get all comments from table with matching challengeID
    command = "SELECT challenge_id,challenge_end_time FROM challenges WHERE active = 1;"
    cur.execute(command)

    # print each comment to output
    queryResult = cur.fetchall()    # returns None if not found
    if queryResult is None:
        print "None"
    else:
        for item in queryResult:
            print "{0},{1}".format(item[0],item[1])
        print # extra line to indicate end

    # close databse connection
    conn.close()
    return

############# MAIN ######################
#addOrgFinal("Awesome Express", "2")
command = sys.argv[1]

if command == "getUser":
    getUser(sys.argv[2])

elif command == "addUser":
    addUser(sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5], sys.argv[6])

elif command == "getAllChallenges":
    getAllChallenges()

elif command == "addChallenge":
    addChallenge(sys.argv[2])

elif command == "updateUserEXP":
    updateUserEXP(sys.argv[2], sys.argv[3])

elif command == "addComment":
    addComment(sys.argv[2], sys.argv[3], sys.argv[4])

elif command == "getComments":
    getComments(sys.argv[2])

elif command == "checkIn":
    checkIn(sys.argv[2], sys.argv[3])

elif command == "chInStatus":
    checkInStatus(sys.argv[2], sys.argv[3])

elif command == "chCount":
    getCheckInCount(sys.argv[2])

elif command == "adOrg":
    addNewOrg(sys.argv[2], sys.argv[3])

elif command == "adU2Org":
    addUserToOrg(sys.argv[2], sys.argv[3])

elif command == "UOList":
    getUserOrgList(sys.argv[2])
    
elif command == "hist":
    getUserHistory(sys.argv[2])

elif command == "rmCH":
    removeChallenge(sys.argv[2])
    
elif command == "check":
    checkChTimes()
    
else:
    print "Invalid Command"
