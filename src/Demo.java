/**
 *
 * @author rchiky
 */
import java.util.Date;
import java.util.Random;

import com.espertech.esper.client.*;
public class Demo {


    public static void startSendingTemperatureReadings(EPRuntime cepRT) {

    	 TemperatureEvent ve = new TemperatureEvent(new Random().nextInt(500), new Date(System.currentTimeMillis()));
    	 try {
    		 Thread.sleep(200);
    		 } catch (InterruptedException e) {
    		 System.out.println("Thread Interrupted "+ e);
    		 }
        // System.out.println("Sending temperature: " + ve);
         cepRT.sendEvent(ve);
    }

    private static class MonitorListener implements UpdateListener {

        @Override
        public void update(EventBean[] newData, EventBean[] oldData) {
        	StringBuilder sb = new StringBuilder();
            sb.append("---------------------------------");
            sb.append("\n- [MONITOR] Average Temp = " + newData[0].getUnderlying());
            sb.append("\n---------------------------------");
            System.out.print(sb.toString());
   
        }
    }

    public static void main(String[] args) {

    	System.out.println("Starting...");
        
        
        Configuration cepConfig = new Configuration();
        cepConfig.addEventType("TemperatureEvent", TemperatureEvent.class.getName());
        
        EPServiceProvider cep = EPServiceProviderManager.getDefaultProvider(cepConfig);
        EPAdministrator cepAdm = cep.getEPAdministrator();
        
        System.out.println("create Monitor Temperature Check Expression");
        String query="select avg(temperature)  from TemperatureEvent.win:time_batch(2 sec)";
        EPStatement monitorStatement = cepAdm.createEPL(query);

        
       
        monitorStatement.addListener(new MonitorListener());

        // Generating events
        long noOfTemperatureEvents = 10000;
        EPRuntime cepRT = cep.getEPRuntime();
        for (int i = 0; i < noOfTemperatureEvents; i++) {
            startSendingTemperatureReadings(cepRT);
        }


    }
}


//String query="select t1.temperature  from pattern"+
//"[Every t1=TemperatureEvent(temperature > 300) -> t2=TemperatureEvent(temperature > 300) -> t3= TemperatureEvent(temperature >300) where timer:within(90 sec)]";