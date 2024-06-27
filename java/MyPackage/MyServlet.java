package MyPackage;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


@SuppressWarnings("serial")
public class MyServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	//API SET UP
		//API Key -: An API KEY is a unique identification number given to every user who is making API calls
		// Jab main API call kruga tab city ka data retrieve kr k(As I am using GET method) the API will send it to me by authenticating the API key
		String apiKey = "1ed4d3f3e4cf6cd56d1d4b4fbccdee5c"; 
		// Get the city from the form input
        String city = request.getParameter("city"); 

        // Create the URL for the OpenWeatherMap API request
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey; //This is the standard URL for searching the weather of a state using the OPENWEATHER API.
     //API SET UP
        try {
            @SuppressWarnings("deprecation")
            
    //API INTEGRATION
			URL url = new URL(apiUrl); // This is used to take String and convert it to URL that is being created above. When we pass apiUrl in this then it is being converted from String to URL.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //This line of code creates an HttpURLConnection object named 'connection' and assigns it the value of the result of calling the 'openConnection()' method on a URL object named 'url'. The 'openConnection()' method establishes a connection to the specified URL and returns a URLConnection object, which is then cast to an HttpURLConnection object.
            connection.setRequestMethod("GET"); //This line of code is setting the request method of a connection to "GET". This means that the connection is being made to retrieve data from the server, rather than sending data to the server. The "GET" method is commonly used for retrieving information from a server in a web application.
            	
               //Reading the data from the network
                InputStream inputStream = connection.getInputStream(); //he get in getInputStream() follows a common naming convention in Java (and other object-oriented languages) where methods that retrieve or access data start with "get".
                InputStreamReader reader = new InputStreamReader(inputStream);
               // System.out.println(reader);
                
                //Scans the content from the stream store it in the StringBuilder.
                Scanner scanner = new Scanner(reader); //  Reader is there in the Scanner because it will be taking input from the reader. System.in takes input from console.
                StringBuilder responseContent = new StringBuilder();// We are not using String because String are immutable so using StringBuilder

                while (scanner.hasNext()) {  // Iterating over the reader using the Scanner and appending over the object. 
                    responseContent.append(scanner.nextLine());
                }
                
               // System.out.println(responseContent);
                scanner.close();
                
                // Parse the data into JSON from String as the response after scanning the data is being stored in StringBuilder. Moreover, we can extract temperature, date, and humidity from the JSON file.
                Gson gson = new Gson(); // It is a google library which allows JSON data into tree model.
                JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);// Data converted from String to JSON
                
                // We have converted the String to JSON as we will not be able to fetch data separately from String. 
                //Date & Time
                long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
                String date = new Date(dateTimestamp).toString();
                
                //Temperature
                double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
                int temperatureCelsius = (int) (temperatureKelvin - 273.15);
               
                //Humidity
                int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
                
                //Wind Speed
                double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
                
                //Weather Condition
                String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
                
                // Set the data as request attributes (for sending to the jsp page)
                request.setAttribute("date", date);
                request.setAttribute("city", city);
                request.setAttribute("temperature", temperatureCelsius);
                request.setAttribute("weatherCondition", weatherCondition); 
                request.setAttribute("humidity", humidity);    
                request.setAttribute("windSpeed", windSpeed);
                request.setAttribute("weatherData", responseContent.toString());
                
                connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Request Dispatcher helps in communicating servelet with a servelt. Forward the request to the weather.jsp page for rendering
        RequestDispatcher rd = request.getRequestDispatcher("index.jsp") ;
        rd.forward(request, response);
    }
}