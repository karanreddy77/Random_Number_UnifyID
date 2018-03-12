import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * @author Karan Reddy
 *
 */
public class Random_Numbers {
	public static void main(String[] args) {
		int height = 128;
		int width = 128;
		int numberOfPixels = height * width * 3;
		List<Integer> randomNumbers = new ArrayList<>();
		while(numberOfPixels > 0) {
			int quota = checkQuota();
			if(quota > 0) {
				String getIntegersURL;
				if(numberOfPixels >= 10000) {
					getIntegersURL = buildGetIntegersURL(10000);
					numberOfPixels -= numberOfPixels;
				} else
					getIntegersURL = buildGetIntegersURL(numberOfPixels);
				List<Integer> tempList = getRandomNumbers(getIntegersURL);
				if(tempList == null) break;
				for(int temp : tempList) {
					randomNumbers.add(temp);
				}
			} else {
				System.out.println("Your Random.org quota is negative");
			}
		}
		if(randomNumbers.size() > 0)
			createRgbBitmap(randomNumbers, height, width);		
	}
	
	/**
	 * A function to check the remaining quota in Random.org
	 * @return quota remaining
	 */
	public static int checkQuota() {
		String checkQuotaURL = "https://www.random.org/quota/?format=plain";
		int result = -1;
		BufferedReader in = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(checkQuotaURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
			    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
			    	result = Integer.parseInt(inputLine);
			}
		} catch (IOException e) {
			System.out.println("There was a problem accessing the URL: " + checkQuotaURL);
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(connection != null) {
				connection.disconnect();
			}
		}
		return result;
	}
	
	/**
	 * A function to build the URL string for Integers GET request
	 * @param num - number of integers required
	 * @return get integers URL
	 */
	public static String buildGetIntegersURL(int num) {
		int minValue = 0;
		int maxValue = 255;
		StringBuilder sb = new StringBuilder("https://www.random.org/integers/?");
		sb.append("num=");
		sb.append(num);
		sb.append("&min=");
		sb.append(minValue);
		sb.append("&max=");
		sb.append(maxValue);
		sb.append("&col=1");
		sb.append("&base=10");
		sb.append("&format=plain");
		return sb.toString();
	}

	/**
	 * A function get the truly random numbers
	 * @param urlString - URL to access
	 * @return list of truly random numbers
	 */
	public static List<Integer> getRandomNumbers(String urlString) {
		List<Integer> content = new ArrayList<>();
		BufferedReader in = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
			    content.add(Integer.parseInt(inputLine));
			}
		} catch (IOException e) {
			System.out.println("There was a problem accessing the URL: " + urlString);
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(connection != null) {
				connection.disconnect();
			}
		}
		return (content != null) ? content : null;
	}
	
	/**
	 * A function to create RGB Bitmap from list of random numbers
	 * @param rgbValue - list of rgb values, height - height of image, width - width of image
	 * @return
	 */
	public static void createRgbBitmap(List<Integer> rgbValues, int height, int width) {
		if(rgbValues == null) return;
	    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    File f = null;
	    Iterator<Integer> it = rgbValues.iterator();
	    int r = 0; //red
        int g = 0; //green
        int b = 0; //blue
	    for(int y = 0; y < height; y++) {
	    	for(int x = 0; x < width * 3; x++) {
	    		if(x % 3 == 0) {
	    			r = (it.hasNext()) ? it.next() : (int) (Math.random() * 256);
	    		} else if(x % 3 == 1) {
	    			g = (it.hasNext()) ? it.next() : (int) (Math.random() * 256);
	    		} else {
	    			b = (it.hasNext()) ? it.next() : (int) (Math.random() * 256);
	    	        int p = (r << 16) | (g << 8) | b; //pixel	   	         
	    	        img.setRGB(x/3, y, p);
	    		}
	    			
	    	}
	    }
	    try {
	   	 String filename = "output_image.png";
	     String workingDirectory = System.getProperty("user.dir");
	   	 StringBuilder absoluteFilePath = new StringBuilder(workingDirectory);
	   	 absoluteFilePath.append(File.separator);
	   	 absoluteFilePath.append("output");
	   	 absoluteFilePath.append(File.separator);
    	 absoluteFilePath.append(filename);
    	 f = new File(absoluteFilePath.toString());
	   	 ImageIO.write(img, "png", f);
	   	 System.out.println("File written to: " + absoluteFilePath.toString());
	    } catch(IOException e) {
	    	System.out.println("Error: " + e);
    	}
	}
}
