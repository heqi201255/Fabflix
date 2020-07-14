function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    var urls = new URL(url);
    var c = urls.searchParams.get(target);
    console.log(c);
    return c;
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
    //console.log("populating movie info");
    //let movieInfoElement = jQuery("#movie_info");
    //movieInfoElement.append("<p>Movie Name: " + resultData[0]["movie_title"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#single_star_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["star_name"] + "</th>";
        rowHTML += "<th>" + resultData[i]["star_birthYear"] + "</th>";
        rowHTML += "<th>"
        for (let m = 0; m < resultData[i]["star_movies"].split(",").length; m++)
        {
            rowHTML += '<a href = "single-movie.html?id='+ resultData[i]["movie_id"].split(",")[m] +'">'
                + resultData[i]["star_movies"].split(',')[m] + '</a>';
            if( m != resultData[i]["star_movies"].split(",").length - 1)
            {
                rowHTML += ",";
            }
        }
        rowHTML += "</th>";
        //rowHTML += "<th>" + resultData[i]["star_movies"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    document.getElementById("return").addEventListener("click", function(){
        window.location.href = sessionStorage.getItem('returnPage');
        $.ajax("api/jump", {
            method: "GET",
            dataType: "json",
            success: (rd) => function () {
                window.location.href = rd["url"];
            }
        })
    });
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let id = getParameterByName("id");
console.log(id);

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + id, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});