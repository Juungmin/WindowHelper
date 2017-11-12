import serial 
import time 
import requests 
import json

firebase_url = 'https://arduino-db-9bf3b.firebaseio.com/'
fixed_interval = 0.01

root_location = 'home test'
zone_location = 'zone_'

first_mq2 = 0
first_mq7 = 0

def main():
    port = serial.Serial("/dev/ttyACM1", baudrate=9600, timeout=None)
    global first_mq2
    global first_mq7
    
    while True:
        line = port.readline();
        arr = line.split() # split by space
 
        print(len(arr))
 
        if len(arr) > 3 :
            data = str(arr[1])#mq2 ratio
            dataType = arr[2]
            data2 = str(arr[3]) #mq2 sensor val
            dataType2 = arr[4]
            data3 = str(arr[6])#mq7 ratio
            data4 = str(arr[8])#mq7 sensor val
        else :
            data = str(arr[1])
            dataType = arr[2]  
   
   
   
	#current time and date
	time_hhmmss = time.strftime('%H:%M:%S')
	date_mmddyyyy = time.strftime('%Y/%m/%d')
        date_location = time.strftime('%Y%m%d')
        
        date_temp = date_mmddyyyy
        
        if date_temp != date_mmddyyyy :
            print("A day has been passed ")
            data_dust_outside = {'date':'0', 'time':'0', 'dust_out_val':'0'}
            data_gas = {'date':'0', 'time':'0', 'gas_mq2':'0', 'sensor_val_mq2': '0', 'gas_mq7':'0', 'sensor_val_mq7': '0'}
            result_dust_out =requests.post(firebase_url + '/' + root_location + '/' + zone_location+date_location +'/dust_outside.json', data=json.dumps(data_dust_outside))
            result_gas = requests.post(firebase_url + '/' + root_location + '/' + zone_location+date_location +'/gas.json', data=json.dumps(data_gas))
        else :
            print("Date is same")
            #dust sensor
            if dataType == 'ug/m3':
                print("Dust density in Pi: %s" % data)
                #insert record
                data_dust_outside = {'date':date_mmddyyyy, 'time':time_hhmmss, 'dust_out_val':str(data)}            
                result_dust_out =requests.put(firebase_url + '/' + root_location + '/' + zone_location+date_location +'/dust_outside.json', data=json.dumps(data_dust_outside))
                print 'Record inserted. Result Code = ' + str(result_dust_out.status_code)+','+result_dust_out.text
            elif dataType == 'mg_2':
                print("Gas_mq2 in Pi: %s" % data)
                
                if first_mq2 == 0 :
                    first_mq2 = data
                
                if first_mq7 == 0 :
                    first_mq7 = data3
 
                #insert record
                data_gas = {'date':date_mmddyyyy, 'time':time_hhmmss, 'gas_mq2':str(data), 'sensor_val_mq2':str(data2), 'gas_mq7':str(data3), 'sensor_val_mq7':str(data4), 'first_mq2':str(first_mq2), 'first_mq7':str(first_mq7)}
                result_gas =requests.put(firebase_url + '/' + root_location + '/' + zone_location+date_location +'/gas.json', data=json.dumps(data_gas))
                print 'Record inserted. Result Code = ' + str(result_gas.status_code)+','+result_gas.text            
 
 
        
        print(zone_location+date_location)
        time.sleep(fixed_interval)

        
        #print("\n\n");
        
        #time.sleep(fixed_interval)
        
if __name__ == "__main__":
        main()

