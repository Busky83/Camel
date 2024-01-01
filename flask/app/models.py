from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

db = SQLAlchemy()

class AlterData(db.Model):
    __tablename__='alterData'
    no = db.Column('No', db.Integer, primary_key=True)
    uuid = db.Column('UUID', db.String)
    temperature = db.Column('Temperature', db.Integer)
    create_dt = db.Column('CreateDt', db.DateTime)

    def from_dict(self, data):
        self.uuid = data.get('uuid')
        self.temperature = data.get('temperature')
        if data.get('timestamp'):
            self.create_dt = datetime.strptime(data.get('timestamp'), '%Y-%m-%dT%H:%M:%S')
        return self