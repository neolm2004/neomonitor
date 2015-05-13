package org.neolm.neomonitor;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.jmx.support.ConnectorServerFactoryBean;

/**
 * Hello world!
 *
 */
public class App 
{
	
	public int i ;
	public int x ;
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ConnectorServerFactoryBean b = new ConnectorServerFactoryBean();
        App[] app = new App[5];
        app[0] = new App();app[1] = new App();app[2] = new App();app[3] = new App();
        app[0].i=0;app[0].x=0;
        app[1].i=1;app[1].x=0;
        app[2].i=3;app[2].x=0;
        app[3].i=2;app[3].x=0;
        System.out.println(new HashSet( Arrays.asList(app)));
        //b.setServiceUrl(serviceUrl);
    }
}
