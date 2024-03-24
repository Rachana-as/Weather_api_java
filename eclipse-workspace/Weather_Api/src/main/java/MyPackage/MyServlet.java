package MyPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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

import org.apache.catalina.valves.JsonAccessLogValve;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//read userinput
		String api_key="730ba97cc3a99602e637331cf308e90a";
		String city=request.getParameter("city");
		String api_url="https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+api_key;
		
		//api integration
		URL url=new URL(api_url);
		HttpURLConnection connection=(HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		
		//read the data from network
		InputStream stream=connection.getInputStream();
		InputStreamReader reader=new InputStreamReader(stream);
		
		//Stringbuilder to store the data because using string we cannot chnage it as string is immutable
		StringBuilder responseContent=new StringBuilder();
		
		//Read data and store in respone
		Scanner sc=new Scanner(reader);
		
		while(sc.hasNext()) {
			responseContent.append(sc.nextLine());
		}
		
		sc.close();
		
		//System.out.println(responseContent);
		
		Gson gson=new Gson();
		JsonObject  jsonObject=gson.fromJson(responseContent.toString(),JsonObject.class);
		
		
		//get Date
		long dateTimeStamp=jsonObject.get("dt").getAsLong()*1000;
		String date=new Date(dateTimeStamp).toString();
		
		//get temp
		double temperature=jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
		int tempCelsius=(int)(temperature - 273.15);
		
		//humidity
		int humidity=jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
		
		double wind=jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
		
		
		String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
		
		request.setAttribute("date",date);
		request.setAttribute("city",city);
		request.setAttribute("temperature",tempCelsius);
		request.setAttribute("weatherCondition",weatherCondition);
		request.setAttribute("humidity",humidity);
		request.setAttribute("windSpeed",wind);
		request.setAttribute("weatherData", responseContent.toString());
		
		connection.disconnect();
		
		
		request.getRequestDispatcher("index.jsp").forward(request, response);
		
		doGet(request, response);
	}

}
