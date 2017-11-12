import serial 
import time 
import requests 
import json

firebase_url = 'https://arduino-db-9bf3b.firebaseio.com/'
fixed_interval = 0.02

root_location = 'home test'
zone_location = 'zone_'


def main():
    port = serial.Serial("/dev/ttyACM0", baudrate=9600, timeout=None)
    while True:
        line = port.readline();
        arr = line.split() # split by space
        
        dataType = arr[2]
        data = str(arr[1])
   
	#current time and date
	time_hhmmss = time.strftime('%H:%M:%S')
	date_mmddyyyy = time.strftime('%Y/%m/%d')

        date_location = time.strftime('%Y%m%d')
        
        date_temp = date_mmddyyyy

        if date_temp != date_mmddyyyy :
            data_dust_inside = {'date':'0', 'time':'0', 'dust_in_val':'0'}
            data_reed = {'date':'0', 'time':'0', 'val':'0', 'desc': ''}
            result_dust_in =requests.post(firebase_url + '/' + root_location + '/' + zone_location+date_location +'/dust_inside.json', data=json.dumps(data_dust_inside))
            result_reed = requests.post(firebase_url + '/' + root_location + '/' + zone_location+date_location +'/reed.json', data=json.dumps(data_reed))
        else :
            if dataType == 'ug/m3':
                print("Dust density in Pi: %s" % data)
                #insert record
                data_dust_inside = {'date':date_mmddyyyy, 'time':time_hhmmss, 'dust_val':str(data)}            
                result_dust_inside =requests.put(firebase_url + '/' + root_location + '/' + zone_location+date_location +'/dust_inside.json', data=json.dumps(data_dust_inside))
                print 'Record inserted. Result Code = ' + str(result_dust_inside.status_code)+','+result_dust_inside.text

            else :
                print("Reed in Pi: %s" % data)
                #insert record
                if dataType == 'closed':
                    data_reed = {'date':date_mmddyyyy, 'time':time_hhmmss, 'val':str(data), 'desc': 'closed'}
                    result_reed =requests.put(firebase_url + '/' + root_location + '/' + zone_location+date_location + '/reed.json', data=json.dumps(data_reed))
                    print 'Record inserted. Result Code = ' + str(result_reed.status_code)+','+result_reed.text            
                else :
                    data_reed = {'date':date_mmddyyyy, 'time':time_hhmmss, 'val':str(data), 'desc': 'opened'}
                    result_reed =requests.put(firebase_url + '/' + root_location + '/' + zone_location+date_location + '/reed.json', data=json.dumps(data_reed))
                    print 'Record inserted. Result Code = ' + str(result_reed.status_code)+','+result_reed.text
                    
                    
        print("\n");
        
        time.sleep(fixed_interval)
        
if __name__ == "__main__":
        main()
