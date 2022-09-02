from base64 import b64encode, b64decode
import hashlib
# import pickle
import bcrypt
from Cryptodome.Cipher import AES
from Cryptodome.Random import get_random_bytes


class encryption_algo():
    def __init__(self, AES_key, bcrypt_key, salt=b'\xf6}\xaep\x80\xf8\xc0\xfe\xc9r\xe9\xa4\xcc_\x03\xa5'):
        self.AES_key = AES_key
        self.bcrypt_key = bcrypt_key
        self.bcrypt_salt = bcrypt.gensalt()
        # self.salt = get_random_bytes(AES.block_size)
        self.salt = salt

        self.private_key = hashlib.scrypt(self.AES_key.encode(), salt=self.salt, n=2**14, r=8, p=1, dklen=32)


    def AES_encrypt(self, plain_text):
        cipher_config = AES.new(self.private_key, AES.MODE_GCM, nonce=None)
        cipher_text = cipher_config.encrypt(bytes(plain_text, 'utf-8'))
        return b64encode(cipher_text).decode('utf-8') + '<~#@#~>'+ b64encode(cipher_config.nonce).decode('utf-8')


    def AES_decrypt(self, cipher_text):
        cipher_text, nonce = tuple(cipher_text.split('<~#@#~>'))
        cipher = AES.new(self.private_key, AES.MODE_GCM, nonce=b64decode(nonce))
        decrypted = cipher.decrypt(b64decode(cipher_text))
        return decrypted.decode('utf-8')


    def bcrypt_encrypt(self, plain_text):
        return bcrypt.hashpw(bytes(plain_text, 'utf-8'), self.bcrypt_salt).decode('utf-8')

    def SHA256_encrypt(self, plain_text):
        return hashlib.sha256(plain_text.encode()).hexdigest()
        


# encryptor = encryption_algo('cervh0s3e2hnpaitaeitad0sn', 'eaia0dnesp3thach2tir0esnv')

# pickle.dump(encryptor, open("encryptor.pkl", 'wb'))

# text = 'Hello World'

# print(text)
# cipher_text = encryptor.AES_encrypt(text)

# print(cipher_text)
# print(encryptor.AES_decrypt(cipher_text))

# print(text)
# cipher_text = encryptor.AES_encrypt(text)

# print(cipher_text)
# print(encryptor.AES_decrypt(cipher_text))

# print(encryptor.bcrypt_encrypt(text))
# print(encryptor.bcrypt_encrypt(text))

