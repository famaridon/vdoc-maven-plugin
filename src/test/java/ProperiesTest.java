import com.vdoc.maven.plugin.ScopedPropertiesMojo;
import com.vdoc.maven.plugin.jaxb.beans.Wrapper;
import com.vdoc.maven.plugin.jaxb.beans.properties.Property;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by famaridon on 07/07/2014.
 */
public class ProperiesTest
{

	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;
	private Marshaller marshaller;

	@Before
	public void init() throws JAXBException
	{
		jaxbContext = JAXBContext.newInstance(Wrapper.class, Property.class);
		unmarshaller = jaxbContext.createUnmarshaller();
		marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
	}

	@Test
	public void unmarshalTest() throws JAXBException
	{
		List<Property> properties = new ArrayList<>();
		Property p = new Property();
		p.setName("com.vdoc");
		p.getValues().put("developement", "val1");
		p.getValues().put("pre-production", "val2");
		p.getValues().put("production","val3");
		properties.add(p);

		marshaller.marshal(new Wrapper<Property>(properties),System.out);
	}

	@Test
	public void MojoTest() throws MojoFailureException, MojoExecutionException
	{
		ScopedPropertiesMojo scopedPropertiesMojo = new ScopedPropertiesMojo();
		scopedPropertiesMojo.setPropertiesXmlFolder(new File("C:\\Users\\famaridon\\IdeaProjects\\vdoc-maven-plugin\\src\\test\\resources"));
		scopedPropertiesMojo.setOutputFolder(new File("C:\\Users\\famaridon\\IdeaProjects\\vdoc-maven-plugin\\src\\test\\resources"));
		scopedPropertiesMojo.setTargetScope("developement");
		scopedPropertiesMojo.execute();

	}

}
