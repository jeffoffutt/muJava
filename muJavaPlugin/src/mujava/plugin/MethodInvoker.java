package mujava.plugin;

import java.lang.reflect.Method;

public class MethodInvoker implements Runnable {

	public Method method;
	public Object invokerObject;
	public Object[] args;
	public Object objectReturned;
	boolean isMutant;

	public MethodInvoker(Method method, Object invokerObject, boolean isMutant, Object... args) {
		this.method = method;
		this.invokerObject = invokerObject;
		this.isMutant = isMutant;
		this.args = args;
	}

	@Override
	public void run() {

		try {					
			objectReturned = method.invoke(invokerObject, args);
		} catch (Exception e) {
			e.printStackTrace();
			/*if (isMutant) {
				System.out.println(method.getName() + ":: mutant resulted into exception! Rejecting mutant!!!");
			} else {
				System.out.println(method.getName() + ":: base program resulted into exception!");
			}*/
		}
		//System.out.println("Notifying " + this);

		synchronized (this) {
			notifyAll();
		}
	}

}
