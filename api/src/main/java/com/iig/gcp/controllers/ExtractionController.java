package com.iig.gcp.controllers;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iig.gcp.CustomAuthenticationProvider;
import com.iig.gcp.extraction.dto.ConnectionDTO;
import com.iig.gcp.extraction.service.ExtractionService;

@Controller
@SessionAttributes(value = { "user", "project", "jwt" })
public class ExtractionController {

	/*
	 * Autowiring for ServiceImpl file
	 */
	@Autowired
	private ExtractionService es;

	@Autowired
	private AuthenticationManager authenticationManager;

	/*
	 * URL to be fetched from properties file
	 * 
	 */
	@Value("${parent.front.micro.services}")
	private String parent_micro_services;

	/*
	 * REST method call to navigate to Home screen
	 */
	/**
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/extraction/ConnectionHome", method = RequestMethod.GET)
	public ModelAndView ConnectionHome() {
		return new ModelAndView("extraction/ConnectionHome");
	}

	/**
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/extraction/Event", method = RequestMethod.GET)
	public ModelAndView Event() {
		return new ModelAndView("extraction/Event");
	}

	/*
	 * GET request call to navigate to Home screen
	 */
	/**
	 * @param modelMap
	 * @param request
	 * @param auth
	 * @return ModelAndView
	 * @throws JSONException
	 */
	@RequestMapping(value = { "/parent" }, method = RequestMethod.GET)
	public ModelAndView parentHome(ModelMap modelMap, HttpServletRequest request, Authentication auth)
			throws JSONException {
		CustomAuthenticationProvider.MyUser m = (CustomAuthenticationProvider.MyUser) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("userId", m.getName());
		jsonObject.put("project", m.getProject());
		jsonObject.put("jwt", m.getJwt());

		modelMap.addAttribute("jsonObject", jsonObject.toString());
		System.out.println("Unix to Parent Token" + m.getJwt());
		return new ModelAndView("redirect:" + "//" + parent_micro_services + "/fromChild", modelMap);

	}

	/*
	 * GET request call to login page
	 */
	/**
	 * @param jsonObject
	 * @param modelMap
	 * @param request
	 * @return ModelAndView
	 * @throws JSONException
	 */
	@RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
	public ModelAndView unixExtractionHome(@Valid @ModelAttribute("jsonObject") String jsonObject, ModelMap modelMap,
			HttpServletRequest request) throws JSONException {

		// Validate the token at the first place
		JSONObject jsonModelObject = null;
		try {

			if (modelMap.get("jsonObject") == null || modelMap.get("jsonObject").equals("")) {
				// TODO: Redirect to Access Denied Page
				return new ModelAndView("/login");
			}
			jsonModelObject = new JSONObject(modelMap.get("jsonObject").toString());
			System.out.println("in Unix Child Token" + jsonModelObject.get("jwt").toString());
			authenticationByJWT(
					jsonModelObject.get("userId").toString() + ":" + jsonModelObject.get("project").toString(),
					jsonModelObject.get("jwt").toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("/login");
			// redirect to Login Page
		}
		request.getSession().setAttribute("user", jsonModelObject.get("userId"));
		request.getSession().setAttribute("project", jsonModelObject.get("project"));

		return new ModelAndView("/index");
	}

	/*
	 * Method used for jwt authentication. Session Variables are authenticated
	 */
	/**
	 * @param name
	 * @param token
	 */
	private void authenticationByJWT(String name, String token) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(name, token);
		Authentication authenticate = authenticationManager.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(authenticate);
	}

	/*
	 * GET Request method call to Load the API Connection page The session
	 * parameters are passed in the modelMap and retrieved in the method. The source
	 * sytem values are loaded in the frontend.
	 */
	/**
	 * @param model
	 * @param request
	 * @return ModelAndView
	 * @throws Exception
	 */
	@RequestMapping(value = "/extraction/ConnectionDetailsUnix", method = RequestMethod.GET)
	public ModelAndView ConnectionDetails(ModelMap model, HttpServletRequest request) throws Exception {

		String src_val = "Api";
		model.addAttribute("src_val", "Api");
		model.addAttribute("user", (String) request.getSession().getAttribute("userid"));
		model.addAttribute("project", (String) request.getSession().getAttribute("project"));

		// Call to fetch existing Source Systems
		ArrayList<String> system = es.getSystem((String) request.getSession().getAttribute("project"));
		model.addAttribute("system", system);
		ArrayList<ConnectionDTO> conn_val = es.getConnectionsAPI(src_val,
				(String) request.getSession().getAttribute("project"));
		model.addAttribute("conn_val", conn_val);

		return new ModelAndView("extraction/ConnectionDetailsUnix");
	}

	/*
	 * POST method call to save the API connection details. This method saves the
	 * DTO object for the API connection and inserts into the database.
	 */
	/**
	 * @param x
	 * @param button_type
	 * @param request
	 * @param redir
	 * @param model
	 * @return ModelAndView
	 * @throws UnsupportedOperationException
	 * @throws Exception
	 */
	@RequestMapping(value = "/extraction/ConnectionDetails1", method = RequestMethod.POST)
	public ModelAndView ConnectionDetails1(@Valid @ModelAttribute("x") String x,
			@ModelAttribute("button_type") String button_type, HttpServletRequest request, RedirectAttributes redir,
			ModelMap model) throws UnsupportedOperationException, Exception {
		String resp = null;
		System.out.println("came her ");

		JSONObject jsonObject = new JSONObject(x);
		jsonObject.getJSONObject("body").getJSONObject("data").put("jwt",
				(String) request.getSession().getAttribute("jwt"));
		x = jsonObject.toString();

		@SuppressWarnings("unused")
		String projectid = (String) request.getSession().getAttribute("project");
		if (button_type.equalsIgnoreCase("add"))
			resp = es.addAPIConnection(x);
		else if (button_type.equalsIgnoreCase("upd"))
			resp = es.updAPIConnection(x);
		else if (button_type.equalsIgnoreCase("del"))
			resp = es.delAPIConnection(x);

		// Response
		String status0[] = resp.toString().split(" ");

		String status = status0[0];
		String message0 = status0[1];
		String message = message0.replaceAll("[\'}]", "").trim();
		String final_message = status + ": " + message;
		if (status.equalsIgnoreCase("Failed")) {
			model.addAttribute("errorString", final_message);
			redir.addFlashAttribute("errorString", resp);
		} else if (status.equalsIgnoreCase("Success")) {
			model.addAttribute("successString", final_message);
			redir.addFlashAttribute("isRefresh", "No");
			redir.addFlashAttribute("successString", resp);
		}

		ArrayList<String> system = es.getSystem((String) request.getSession().getAttribute("project"));
		model.addAttribute("system", system);

		// to reload the screen
		String src_val = "Api";
		ArrayList<ConnectionDTO> conn_val = es.getConnectionsAPI(src_val,
				(String) request.getSession().getAttribute("project"));
		model.addAttribute("conn_val", conn_val);

		return new ModelAndView("redirect:" + "/extraction/ConnectionDetailsUnix");
	}

	/*
	 * Request Method to POST the edit screen details
	 * 
	 */

	/**
	 * @param src_val
	 * @param conn
	 * @param model
	 * @param request
	 * @return ModelAndView
	 * @throws UnsupportedOperationException
	 * @throws Exception
	 */
	@RequestMapping(value = "/extraction/ConnectionDetailsEditUnix", method = RequestMethod.POST)
	public ModelAndView ConnectionDetailsEdit(@Valid @ModelAttribute("src_val") String src_val,
			@Valid @ModelAttribute("conn") int conn, ModelMap model, HttpServletRequest request)
			throws UnsupportedOperationException, Exception {

		src_val = "Api";
		model.addAttribute("src_val", src_val);
		// UserAccount u = (UserAccount) request.getSession().getAttribute("user");
		model.addAttribute("user", request.getSession().getAttribute("userid"));
		model.addAttribute("project", (String) request.getSession().getAttribute("project"));
		ArrayList<String> system = es.getSystem((String) request.getSession().getAttribute("project"));
		model.addAttribute("system", system);
		ConnectionDTO conn_val = es.getConnections2API(src_val, conn,
				(String) request.getSession().getAttribute("project"));
		model.addAttribute("conn_val", conn_val);

		return new ModelAndView("extraction/ConnectionDetailsEditUnix");

	}
}
