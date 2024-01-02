from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

db = SQLAlchemy()

class AlterData(db.Model):
    __tablename__='alterdata'
    no = db.Column('no', db.Integer, primary_key=True)
    uuid = db.Column('uuid', db.String)
    temperature = db.Column('temperature', db.Integer)
    create_dt = db.Column('createdt', db.DateTime)

    def from_dict(self, data):
        self.uuid = data.get('uuid')
        self.temperature = data.get('temperature')
        if data.get('timestamp'):
            self.create_dt = datetime.strptime(data.get('timestamp'), '%Y-%m-%dT%H:%M:%S')
        return self
    
    def __repr__(self):
        return '<UUID %r>' % self.uuid