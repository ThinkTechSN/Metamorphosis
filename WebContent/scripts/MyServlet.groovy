@WebServlet("/test.html")
class MyServlet {

	def get() {		
		println "hello world from a groovy servlet"
		json([status:1])
	}
	
	def post() {
	   	println "hello $request.name"
		json([status:1])
	}
		
}