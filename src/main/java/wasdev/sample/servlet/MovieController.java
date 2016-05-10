package wasdev.sample.servlet;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movie")
public class MovieController
{

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String getMovie()
	{
		return "list";

	}

}
