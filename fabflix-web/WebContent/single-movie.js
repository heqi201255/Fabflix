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
    let movieTableBodyElement = jQuery("#single_movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        rowHTML += "<th>"
        if(resultData[i]["movie_stars"] == null){}
        else{
        for (let m = 0; m < resultData[i]["movie_stars"].split(",").length; m++)
        {
            rowHTML += '<a href = "single-star.html?id='+ resultData[i]["star_id"].split(",")[m] +'">'
                + resultData[i]["movie_stars"].split(',')[m] + '</a>';
            if( m !== resultData[i]["movie_stars"].split(",").length - 1)
            {
                rowHTML += ",";
            }
        }}
        rowHTML += "</th>";

        rowHTML += "<th>";
        if(resultData[i]["movie_genre"] == null){

        }
        else{
        for (let m = 0; m < resultData[i]["movie_genre"].split(",").length; m++)
        {
            rowHTML += '<a href = "movies.html?gid='+ resultData[i]["movie_gid"].split(",")[m] +'">'
                + resultData[i]["movie_genre"].split(',')[m] + '</a>';
            if( m !== resultData[i]["movie_genre"].split(",").length - 1)
            {
                rowHTML += ",";
            }
        }}
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "<th><button id='addCartButton_"+resultData[i]["movie_id"]+"'>Add to Cart</button></th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
        document.getElementById("addCartButton_"+resultData[i]["movie_id"]).addEventListener("click", function(){
            // $.ajax("api/cart", {
            //     method: "POST",
            //     data: "item="+resultData[i]["movie_id"]+"&req=add",
            //     success: function (resultData){
            //         alert(resultData['message']);
            //     }
            // })
            if (sessionStorage.getItem("cart_"+resultData[i]["movie_id"])){
                sessionStorage.setItem("cart_"+resultData[i]["movie_id"], (parseInt(sessionStorage.getItem("cart_"+
                    resultData[i]["movie_id"]).split("_")[0]) + 1).toString()+"_"+
                    sessionStorage.getItem("cart_"+resultData[i]["movie_id"]).split("_")[1]);
            } else {
                sessionStorage.setItem("cart_"+resultData[i]["movie_id"], (1).toString()+"_"+Math.round(Math.random() * 100));
            }
            if (!sessionStorage.getItem("cartNum")){
                sessionStorage.setItem("cartNum", (1).toString());
            } else {
                sessionStorage.setItem("cartNum", (parseInt(sessionStorage.getItem("cartNum"))+1).toString())
            }
            alert("Successfully added item")
        });
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


let id = getParameterByName("id");
console.log(id);

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + id, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});