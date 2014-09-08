import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * Created by famaridon on 04/07/2014.
 */
public class Main
{
	public static void main(String[] args)
	{
		try (FileInputStream jarFileInputStream = new FileInputStream("D:\\apache-maven-3.2.1\\bin\\VDocPlatformSDKClient-suite.jar");
			 JarInputStream jarInputStream = new JarInputStream(jarFileInputStream);

			 FileOutputStream javadocFileOutputStream = new FileOutputStream("D:\\apache-maven-3.2.1\\bin\\VDocPlatformSDKClient-javadoc.jar");
			 JarOutputStream javadocOutputStream = new JarOutputStream(javadocFileOutputStream);

			 FileOutputStream sourceFileOutputStream = new FileOutputStream("D:\\apache-maven-3.2.1\\bin\\VDocPlatformSDKClient-source.jar");
			 JarOutputStream sourceOutputStream = new JarOutputStream(sourceFileOutputStream);
		)
		{

			ZipEntry archiveEntry;
			while ((archiveEntry = jarInputStream.getNextEntry()) != null)
			{
				if ( archiveEntry.getName().startsWith("apidocs/") &&  !archiveEntry.getName().equals("apidocs/") )
				{
					System.out.println("javadoc : " + archiveEntry.getName());

					ZipEntry newEntry = new ZipEntry(StringUtils.substringAfter(archiveEntry.getName(),"apidocs/"));
					copyEntry(jarInputStream, javadocOutputStream, archiveEntry, newEntry);
				} else if ( archiveEntry.getName().endsWith(".java") )
				{
					System.out.println("source : " + archiveEntry.getName());

					ZipEntry newEntry = new ZipEntry(archiveEntry.getName());
					copyEntry(jarInputStream, sourceOutputStream, archiveEntry, newEntry);
				} else
				{
					System.out.println("skip : " + archiveEntry.getName());
					if ( archiveEntry.getSize() > 0 )
					{
						jarInputStream.skip(archiveEntry.getSize());
					}
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	protected static void copyEntry(JarInputStream jarInputStream, JarOutputStream javadocOutputStream, ZipEntry archiveEntry, ZipEntry newEntry) throws IOException
	{
		javadocOutputStream.putNextEntry(newEntry);
		if ( archiveEntry.getSize() > 0 )
		{
			newEntry.setSize(archiveEntry.getSize());
			byte[] bytes = new byte[2048];
			int read = 0;
			while ((read += jarInputStream.read(bytes, 0, nextRead(archiveEntry.getSize(), read, bytes.length))) < archiveEntry.getSize())
			{
				javadocOutputStream.write(bytes);
			}
		}
		javadocOutputStream.closeEntry();
	}

	protected static int nextRead(long total, int read, int buffer)
	{
		int next = (int) total - read;
		return next > buffer ? buffer : next;
	}

}
