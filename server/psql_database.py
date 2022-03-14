import psycopg2
import random
import string

class Database:
    def __init__(self,host,user,passwd,database):
        self.db = psycopg2.connect(host=host,
                                     user=user,
                                     password=passwd,
                                     database=database)


        self.cursor = self.db.cursor()

        self.cursor.execute('''CREATE TABLE IF NOT EXISTS USER_INFO (
                          id VARCHAR(20) UNIQUE ,
                          name VARCHAR(20) NOT NULL , 
                          age VARCHAR(20) ,
                          address VARCHAR(20) NOT NULL ,
                          contact_no VARCHAR(20) NOT NULL ,
                          blood_grp VARCHAR(20) NOT NULL ,
                          embed1 text[],
                          embed2 text[],
                          embed3 text[],
                          PRIMARY KEY (id));
        ''')

        self.cursor.execute('''CREATE TABLE IF NOT EXISTS USER_LOGIN ( 
                          mail_id VARCHAR(30) UNIQUE ,
                          user_name VARCHAR(20) UNIQUE ,
                          password VARCHAR(20) NOT NULL ,
                          id VARCHAR(20) UNIQUE ,
                          PRIMARY KEY (id));
        ''')

        self.cursor.execute('''CREATE TABLE IF NOT EXISTS USER_LOG ( 
                          id VARCHAR(20) UNIQUE ,
                          check_in VARCHAR(10000),
                          check_out VARCHAR(10000),
                          PRIMARY KEY (id));
        ''')

        self.cursor.close()
        self.cursor = self.db.cursor()
        self.db.commit()
        

    def generate_user_id(self): # unique id varies from 8-15 characters
        unique_id = ''
        for i in range(random.randrange(8,15)): 
            unique_id = unique_id + random.choice(string.printable[:61])

        return unique_id

    def check_user_id_exist(self, user_id):
        exist = False
        self.cursor.execute("SELECT id FROM USER_LOGIN")
        for i in self.cursor:
            if user_id == i[0]:
                exist = True
                break

        self.cursor.close()
        self.cursor = self.db.cursor()
        return exist

    def check_unique_data(self, data):
        check = True
        self.cursor.execute("SELECT mail_id , user_name FROM USER_LOGIN")
        for i in self.cursor:
            #mail_id , user_nam
            if data[0] == i[0] or data[1] == i[1]:
                check = False
                break
        self.cursor.close()
        self.cursor = self.db.cursor()
        return check

    def add_db(self, data):
        user_name, password, mail_id, name, age, user_id, address, contact_no, blood_grp, embed1, embed2, embed3 = data
        self.cursor.execute('INSERT INTO USER_LOGIN (mail_id , user_name , password , id) VALUES (%s, %s, %s, %s)',(mail_id, user_name, password, user_id))
        self.cursor.execute('INSERT INTO USER_INFO (id , name , age , address , contact_no , blood_grp, embed1, embed2, embed3) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)',(user_id, name, age, address, contact_no, blood_grp, embed1, embed2, embed3))
        self.cursor.execute('INSERT INTO USER_LOG (id , check_in, check_out) VALUES (%s , %s, %s)', (user_id, '', ''))
        self.db.commit()
        self.cursor.close()
        self.cursor = self.db.cursor()
        return 1

    def get_embeds(self, user_id):
        details = None
        self.cursor.execute("SELECT embed1, embed2, embed3 FROM USER_INFO WHERE id = %s", (user_id,))

        for i in self.cursor:
            details = i

        details = list(details)
        self.cursor.execute("SELECT log FROM USER_LOG WHERE id = %s", (user_id,))
        
        for i in self.cursor:
            details.append(i[0])

        self.cursor.close()
        self.cursor = self.db.cursor()
        
        return details

    def update_db(self, data):
        user_name, password, mail_id, name, age, user_id, contact_no, address, blood_grp = data
        self.cursor.execute('UPDATE USER_LOGIN set mail_id = %s, user_name = %s, password = %s WHERE id = %s',(mail_id, user_name, password, user_id))
        self.cursor.execute('UPDATE USER_INFO set name = %s, age = %s, address = %s, contact_no = %s, blood_grp = %s WHERE id = %s',(name, age, address, contact_no, blood_grp, user_id))
        self.db.commit()
        self.cursor.close()
        self.cursor = self.db.cursor()
        return 1

    def sign_up(self, data):
        mail_id, user_name, password, name, age, address, contact_no, blood_grp, embed1, embed2, embed3 = data
        user_name_availablity = self.check_unique_data((mail_id, user_name))
        unique_id = None
        if user_name_availablity:
            while True:
                unique_id = self.generate_user_id()
                if self.check_user_id_exist(unique_id):
                    continue
                
                else :
                    self.add_db((user_name, password, mail_id, name, age, unique_id, address, contact_no, blood_grp, embed1, embed2, embed3))
                    break

        return  user_name_availablity, unique_id

    
    def check_credentials(self, data, user_name_or_mail_id): # data = [username, password]
        id_psswrd = None
        pswrd_incorrect = 0
        if user_name_or_mail_id == 'username' :
            self.cursor.execute('SELECT id , password FROM USER_LOGIN WHERE user_name = %s ', (data[0], ) )
            for i in self.cursor:
                id_psswrd = i

        elif user_name_or_mail_id == 'mail_id' :
            self.cursor.execute('SELECT id , password  FROM USER_LOGIN WHERE mail_id = %s ', (data[0],) )
            for i in self.cursor:
                id_psswrd = i

        self.cursor.close()
        self.cursor = self.db.cursor()
        if len(id_psswrd) == 2: # id, password
            if id_psswrd[1] == data[1]:
                return id_psswrd[0] # returns id
            else :
                return  pswrd_incorrect # incorrect password

        else:
            #username doesnt exist
            return 3


    def user_login_details(self, data, type_of_login):
        user_name_or_mail_id , password = data[0] , data[1]

        if type_of_login == 'username':
            response = self.check_credentials((user_name_or_mail_id, password),'username')

        else:
            response = self.check_credentials((user_name_or_mail_id, password),'mail_id')

        return response


    def get_user_details(self, user_id):
        details = None
        self.cursor.execute("SELECT name, age, address, contact_no, blood_grp FROM USER_INFO WHERE id = %s", (user_id,))

        for i in self.cursor:
            details = i

        details = list(details)
        self.cursor.execute("SELECT check_in, check_out FROM USER_LOG WHERE id = %s", (user_id,))
        
        for i in self.cursor:
            details.append(i[0])

        self.cursor.close()
        self.cursor = self.db.cursor()
        
        return details

    def master_command(self, query):
        self.cursor.execute(query)
        output = ""

        for i in self.cursor:
            for j in i:
                if isinstance(j, list):
                    j = "".join(j)
                output = output + " | " + j

            output = output + '\n'

        self.cursor.close()
        self.cursor = self.db.cursor() 

        return output
      

    def update_log(self, user_id, check_in, check_out):
        self.cursor.execute("SELECT check_in FROM USER_LOG WHERE id = %s", (user_id,))

        log = '' # Hour:Minute:Second@Date.Month.Year-Hour:Minute:Second@Date.Month.Year Hour:Minute:Second@Date.Month.Year-Hour:Minute:Second@Date.Month.Year
        for i in self.cursor:
            log = i

        log = log[0]
        log  = log + check_in

        self.cursor.execute("UPDATE USER_LOG set check_in = %s WHERE id = %s", (check_in, user_id))

        self.cursor.execute("SELECT check_out FROM USER_LOG WHERE id = %s", (user_id,))

        log = '' # Hour:Minute:Second@Date.Month.Year-Hour:Minute:Second@Date.Month.Year Hour:Minute:Second@Date.Month.Year-Hour:Minute:Second@Date.Month.Year
        for i in self.cursor:
            log = i

        log = log[0]
        log  = log + check_out

        self.cursor.execute("UPDATE USER_LOG set check_out = %s WHERE id = %s", (check_out, user_id))

        self.cursor.close()
        self.db.commit()  
        self.cursor = self.db.cursor()   
            






