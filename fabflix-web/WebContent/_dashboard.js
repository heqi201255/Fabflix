
function handleResult(resultData){
    let metadataTableBodyElement = jQuery("#metadata_table_body");
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["table_name"] + "</th>";
        rowHTML += "<th>" + resultData[i]["attribute_name"] + "</th>";
        rowHTML += "<th>" + resultData[i]["attribute_type"] + "</th>";
        rowHTML += "<th>" + resultData[i]["attribute_size"] + "</th>";
        rowHTML += "</tr>";
        metadataTableBodyElement.append(rowHTML);
    }
}

function info_after_submit(resultData){
    console.log(resultData);
    alert("Star ID: " + resultData[0]["new_sid"] + " Star name: " + resultData[0]["new_name"] + " Star birth year: " + resultData[0]["new_year"] + "\nStar Information has been inserted successfully");
    location.reload();
}

function movie_info_after_submit(resultData){
    console.log(resultData);
    if (resultData[0]["mid"] === "same"){
        alert("The movie has already existed");
        location.reload();
    }
    else{
        alert("Movie ID: " + resultData[0]["mid"] + " GenreId: " + resultData[0]["genre"] + " StarId: " + resultData[0]["star"] + "\nhas successfully inserted");
        location.reload();
    }
}

function InsertStar(){
    var sname = document.getElementById("sname").value;
    var syear = document.getElementById("syear").value;
    console.log(sname);
    console.log(syear);

    if(!sname){
        alert("Enter the Star's name!");
        location.reload();
    }
    if (!syear){
        syear = null;
    }

    var star_info = {
        "sname" : sname,
        "syear" : syear
    }

    jQuery.ajax({
        type: "GET",
        dataType: "json",
        data: star_info,
        url: "api/addstar",
        success: resultData => info_after_submit(resultData)
    });
}

function InsertMovie(){
    var mtitle = document.getElementById("mtitle").value;
    var myear = document.getElementById("myear").value;
    var mdirector = document.getElementById("mdirector").value;
    var mgenre = document.getElementById("mgenre").value;
    var mstar = document.getElementById("mstar").value;
    console.log(mtitle);
    console.log(myear);
    console.log(mdirector);
    console.log(mgenre);
    console.log(mstar);

    if (!mtitle || !myear || !mdirector || !mgenre || !mstar){alert("Please enter all the required field"); location.reload();}

    var movie_info = {
        "mtitle" : mtitle,
        "myear" : myear,
        "mdirector" : mdirector,
        "mgenre" : mgenre,
        "mstar" : mstar
    }
    jQuery.ajax({
        type: "GET",
        dataType: "json",
        data: movie_info,
        url: "api/addmovie",
        success: resultData => movie_info_after_submit(resultData)
    });
}




jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/dashboard", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});