let login_form = $("#login_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 * @param is_employee boolean
 */
function handleLoginResult(resultDataString, is_employee) {
    resultDataJson = JSON.parse(resultDataString);
    console.log("handle login response");

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        if (is_employee === true){
            window.location.replace("_dashboard.html");
        }
        else{
            window.location.replace("main.html");
        }
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        alert(resultDataJson["message"]);
        grecaptcha.reset();
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    let check = document.getElementById("employeeCheck");
    console.log(check.checked);
    var recaptcha = grecaptcha.getResponse();
    if(recaptcha.length === 0){
        alert("Click Recaptcha for verification please");
    }

    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    if(check.checked === true){
        console.log("employee");
        $.ajax(
            "api/employee-login", {
                method: "POST",
                // Serialize the login form to the data sent by POST request
                data: login_form.serialize(),
                success: (resultDataString) => handleLoginResult(resultDataString, true)
            }
        );
    }
    else
    {
        console.log("customer");
        console.log(login_form.serialize())
        $.ajax("api/login", {
                method: "POST",
                // Serialize the login form to the data sent by POST request
                data: login_form.serialize(),
                success: (resultDataString) => handleLoginResult(resultDataString, false)
            }
        );
    }
}


// Bind the submit action of the form to a handler function
login_form.submit(submitLoginForm);