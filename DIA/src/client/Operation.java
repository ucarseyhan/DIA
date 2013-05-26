package client;

import java.util.Random;


public enum Operation {
	JOIN, 			//Join client
	DELETE, 		//Delete client
	HELLO,  		//HELLO Messages
	DEFAULT, 		//Default operation
	INTERACT;       //Interact with another client
	@Override
	public String toString() 
	{
		// only capitalize the first letter
		String s = super.toString();
		return s.substring(0, 1) + s.substring(1).toLowerCase();
	}
	
    public static final RandomEnum<Operation> r = new RandomEnum<Operation>(Operation.class);
    @SuppressWarnings("rawtypes")
	static class RandomEnum<E extends Enum> 
    {

        private static final Random RND = new Random();
        private final E[] values;

        public RandomEnum(Class<E> token) {
            values = token.getEnumConstants();
        }

        public E random() {
            return values[RND.nextInt(values.length)];
        }
    }
}
