package book.cpps.mechatronicsagents.horizontal;

public class Main {

    public static void main(String[] args) {
        Thread mainContainerThread = new Thread(() -> {
            String[] jadeArgs = {
                "-gui",
                "dealer1:book.cpps.mechatronicsagents.horizontal.CarDealer;"
            };
            jade.Boot.main(jadeArgs);
        });
        mainContainerThread.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Thread container1Thread = new Thread(() -> {
            String[] jadeArgs = {
                "-container",
                "car:book.cpps.mechatronicsagents.horizontal.Car"
            };
            jade.Boot.main(jadeArgs);
        });
        container1Thread.start();


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Thread container2Thread = new Thread(() -> {
            String[] jadeArgs = {
                "-container",
                "compFactory1:book.cpps.mechatronicsagents.horizontal.ComponentFactory;"
            };
            jade.Boot.main(jadeArgs);
        });
        container2Thread.start();


	    try {
	        Thread.sleep(3000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }

	    // Start the factories container in another separate thread
	    Thread container3Thread = new Thread(() -> {
	        String[] jadeArgs = {
	            "-container",
	            "factory1:book.cpps.mechatronicsagents.horizontal.Factory;"
	        };
	        jade.Boot.main(jadeArgs);
	    });
	    container3Thread.start();
	    
	    try {
	        Thread.sleep(3000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	
    
    // Start the factories container in another separate thread
	    Thread container4Thread = new Thread(() -> {
	        String[] jadeArgs = {
	            "-container",
	            "Carfactory1:book.cpps.mechatronicsagents.horizontal.CarFactory;"
	        };
	        jade.Boot.main(jadeArgs);
	    });
	    container4Thread.start();		
	    
	    try {
	        Thread.sleep(3000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
    }
}
}


