import json
import uuid # this is for generating unique id
import datetime
import bottle
from bottle import route, run, request, abort
from pymongo import Connection
from math import radians, sin, cos, asin, sqrt
from bson import json_util
#The encoder is required to serialize the datetime object since json does not provide with any standard for dates.
class MyEncoder(json.JSONEncoder):
    
    def default(self, obj):
        if isinstance(obj, datetime.datetime):
            return (obj.isoformat(' '))
        
        return json.JSONEncoder.default(self, obj)

def haversine(lon1, lat1, lon2, lat2):
    """
        Calculate the great circle distance between two points
        on the earth (specified in decimal degrees)
        """
    # convert decimal degrees to radians
    #print lon1 , lat1 , lon2 , lat2
    lon1, lat1, lon2, lat2 = map(radians, [float(lon1), float(lat1), float(lon2), float(lat2)])
    #print lon1 , lat1 , lon2 , lat2
    # haversine formula
    dlon = lon2 - lon1
    dlat = lat2 - lat1
    #print dlat, dlon
    a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
    c = 2 * asin(sqrt(a))
    mtr = 6367000 * c
    #print mtr
    return mtr
#here we are making connection to the MongoDB server running on the same localhost as the python server.
connection = Connection('localhost', 27017)
#A connection to our gatortracker database is opened up.
db = connection.gatortracker
#These are the names of the collections whicha are used. 
db_user_feed = "user_feed"
db_bus_locations = "bus_locations"
db_users = "users"

#each REST API call can be received easily and assigned to task using @route 
@route('/documents', method='PUT')
def put_document():
	data = request.body.readline()
	if not data:
		abort(400, 'No data received')
	entity = json.loads(data)
	if not entity.has_key('_id'):
		abort(400, 'No _id specified')
	try:
		db['documents'].save(entity)
	except ValidationError as ve:
		abort(400, str(ve))
	
@route('/documents/:id', method='GET')
def get_document(id):
	entity = db['documents'].find_one({'_id':id})
	if not entity:
		abort(404, 'No document with id %s' % id)
	return entity

#This is the API called when a user calls the server for the very first time. Here we are generating a unique user id to each user.
@route('/startSession', method = 'GET')
def startSession():
    #here we need to create a unique id and store it in the database.
    date = str(datetime.datetime.utcnow());
    id = str(uuid.uuid4())
    reply = {'date' : date,
                'user_id': id
                }
    
    response = {'date' : date,
        'user_id': id
        }
    return_id = db[db_users].save(reply)

#print 'the id returned is', return_id
#print 'the changed reply is',reply
#print 'the NON changed respponse is ',response
    return response

#This api receives the location from the bus location feeders
#here the data is parsed adn stored in the database.
@route('/set_bus_location', method = 'PUT')
def set_bus_location():
    date = datetime.datetime.utcnow()
    data = request.body.readline()
    print 'data is ',data
    if not data:
		abort(400, 'No data received')
    entity = json.loads(data)
    entity['date'] = date
    db[db_user_feed].save(entity)

#This is the most important part of the whole system. A get bus location call .
@route('/get_bus_location', method = 'POST')
def get_bus_location():
    #time fresh stands for the fresh queries in terms of seconds searched in the user feed collection.
    time_fresh = 10
    #time_fresh_bus is similar to time_fresh but for querying the bus_locations collection. 
    time_fresh_bus = 25
    time_start = datetime.datetime.utcnow()
    print datetime.datetime.utcnow()
    # Here we are creating a tiem query element for the user_feed query for teh past "time_fresh" seconds
    date_query = time_start-datetime.timedelta(seconds=time_fresh)
    data = request.body.readline()
    entity = json.loads(data)
    route_id = entity['route_id']
    print route_id
    # These thresholds stands for the distance between two points of users and buses for the following values respectively
    cluster_threshold = 100
    mapping_threshold = 220
    groups = []
    reply = list(db[db_user_feed].find({"date":{"$gt":date_query},"route_id":route_id} ,{"date":1,"route_id":1,"loc":1,"_id":0}))
    unassigned = range(len(reply))
    print "reply",reply
    j=0
    print j , unassigned
    while len(unassigned) != 0:
        i = unassigned[0]
        print i
        groups.append(reply[i])
        print j , unassigned
        #USER CLUSTURING ALGORITHM
        #[:] allows to modify the sequecnce being iterated
        for test in unassigned[:]:
            print "test", test
            distance = haversine(reply[test]['loc']['lng'],reply[test]['loc']['lat'],reply[i]['loc']['lng'],reply[i]['loc']['lat'])
            print i,test,distance
            if distance < cluster_threshold:
                unassigned.remove(test)
                if groups[j]['date'] < reply[test]['date']:
                    groups[j] = reply[test]
        #the id needs to be unique for a particular iteration bus_id = date+time+route_id+j
        # any bus id can be picked up and searched for where was the bus all the time .
        #Only problem is that number of digits of bus id should be fixed or else extracting j is difficult. Or j should be fixed digits
        groups[j]['bus_id'] = groups[j]['date'].strftime("%s") + str(groups[j]['route_id']) + str(j)
        j += 1
        print j , unassigned
    #update the insert is working
    update = []
            #following is the way in which the user feed data is queried.
    date_query = date_query-datetime.timedelta(seconds=time_fresh_bus)
    bus_list = list(db[db_bus_locations].find({"date":{"$gt":date_query},"route_id":route_id} ,{"bus_id":1,"date":1,"route_id":1,"loc":1,"_id":0}))
    for bus1 in bus_list:
        print bus1
    if len(bus_list) == 0:
        print "length is zero"
        update = groups
    else:
        #BUS MAPPING ALGORITHM
        for new_bus in groups[:]:
            for old_bus in bus_list[:]:
                distance = haversine(old_bus['loc']['lng'],old_bus['loc']['lat'],new_bus['loc']['lng'],new_bus['loc']['lat'])
                print distance, old_bus , new_bus 
                if distance < mapping_threshold:
                    new_bus['bus_id']= old_bus['bus_id']
                    #Update the database with the entry only if the new bus is latest than the old bus
                    if new_bus['date'] < old_bus['date']:
                        groups.remove(new_bus)
                    bus_list.remove(old_bus)
                    break
        update = groups
    result = {}
    result['bus_info'] = groups
    final_result = json.dumps(result, cls = MyEncoder)
#    for item in groups:
##        json_doc = json.dumps(item, default=json_util.default)
#        item['date'] = item['date'].isoformat(' ')
#        result['bus_info'].append(item)
#        print item
    print result
    print update
    if len(update)>0:
        db[db_bus_locations].insert(update)
    
    return final_result

    
#    return {"json1":[{"status":"ok"},{"status":"error"}]}

run(host='0.0.0.0', port=8080)
