from flask import Flask, request
from config import app_config
from app.models import db, AlterData
from sqlalchemy.sql import text

db_config = app_config.POSTGRES

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = f'postgresql://{db_config["USER"]}:{db_config["PASSWORD"]}@{db_config["HOST"]}:{db_config["port"]}/{db_config["DB"]}'
db.app = app
db.init_app(app)

@app.route('/')
def home():
    return "Home Page"

@app.route('/getAlter', methods=['GET'])
def getAlter():
    # query_data = db.session.execute(text('select * from "alterData"'))
    rtn = AlterData.query.all()
    print(rtn)
    return "OK"

@app.route('/insertAlter', methods=['POST'])
def insertAlter():
    if request.is_json:
        data = request.get_json()
        row = AlterData().from_dict(data)
        db.session.add(row)
        db.session.commit()
    else:
        return "Please Use Json Format"
    return "ok"

if __name__ == "__main__":
    app.run(debug=True, port=5500)