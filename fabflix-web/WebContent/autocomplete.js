var storedData = []

function handleLookup(query, doneCallback) {

    var found = false;
    for(let i = 0; i < storedData.length; i++){
        if(storedData[i].query === query){
            found = true;
            console.log("get data from front end");
            console.log(storedData[i].data);
            doneCallback({suggestions: storedData[i].data});
        }
    }
    if (found === false){
        console.log("autocomplete initiated")
        console.log("sending AJAX request to backend Java Servlet")
        console.log("Get data from back end")


    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/auto-complete?search=" + escape(query),
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    })}
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = JSON.parse(data);

    let obj = {
        query : query,
        data : jsonData
    }
    console.log(jsonData);

    if(storedData.length >= 20){
        storedData.shift();
    }
    storedData.push(obj);


    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion
    sessionStorage.setItem("returnPage", window.location.pathname+window.location.search);
    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["ID"]);
    location.href = "single-movie.html?id="+suggestion["data"]["ID"];
}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#search').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    minChars : 3,
    showNoSuggestionNotice: true
});


/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    sessionStorage.setItem("returnPage", window.location.pathname+window.location.search);
    location.href = "movies.html?search="+query+"&sort=r_dsc_t_asc&pagenum=25&offset=0";
}

// bind pressing enter key to a handler function
$('#search').keypress(function(event) {

    // keyCode 13 is the enter key
    if (event.keyCode === 13) {
        event.preventDefault();
        handleNormalSearch($('#search').val())
    }
})

