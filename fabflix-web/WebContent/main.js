
function handleBrowseResult(resultData){
    if (!sessionStorage.getItem("cartNum")){
        sessionStorage.setItem("cartNum", 0);
    }
    let genreTableBodyElement = jQuery("#genre_table_body");

    for (let i = 0; i < resultData.length;)
    {
        let rowHTML = "";
        rowHTML += "<tr>";
        for (let m = 0; m < 3; m ++){
            if (i == resultData.length){
                break;
            }
            rowHTML += "<th>" + '<a href = "movies.html?gid='+ resultData[i]["gid"] + '&sort=r_dsc_t_asc&pagenum=25&offset=0">'+ resultData[i]["genre"] + '</a>' + "</th>";
            i++;
        }
        rowHTML += "</tr>"
        genreTableBodyElement.append(rowHTML);
    }

    let genreTitleTableBody = jQuery("#genre_title_table_body");
    for (let m = 48; m <= 57; m++){
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href = "movies.html?title=' + String.fromCharCode(m) + '&sort=r_dsc_t_asc&pagenum=25&offset=0">' + String.fromCharCode(m).toUpperCase() + '</a>' + "</th>";
        m++;
        rowHTML += "<th>" + '<a href = "movies.html?title=' + String.fromCharCode(m) + '&sort=r_dsc_t_asc&pagenum=25&offset=0">' + String.fromCharCode(m).toUpperCase() + '</a>' + "</th>";
        m++;
        rowHTML += "<th>" + '<a href = "movies.html?title=' + String.fromCharCode(m) + '&sort=r_dsc_t_asc&pagenum=25&offset=0">' + String.fromCharCode(m).toUpperCase() + '</a>' + "</th>";
        m++;
        rowHTML += "<th>" + '<a href = "movies.html?title=' + String.fromCharCode(m) + '&sort=r_dsc_t_asc&pagenum=25&offset=0">' + String.fromCharCode(m).toUpperCase() + '</a>' + "</th>";
        m++;
        rowHTML += "<th>" + '<a href = "movies.html?title=' + String.fromCharCode(m) + '&sort=r_dsc_t_asc&pagenum=25&offset=0">' + String.fromCharCode(m).toUpperCase() + '</a>' + "</th>";
        rowHTML += "</tr>";
        genreTitleTableBody.append(rowHTML);
    }
    for (let m = 65; m <= 90; m++){
        let rowHTML = "";
        if (m === 90)
        {
            rowHTML += "<tr>";
            rowHTML += "<th>" + '<a href = "movies.html?title=' + String.fromCharCode(m) + '&sort=r_dsc_t_asc&pagenum=25&offset=0">' + String.fromCharCode(m).toUpperCase() + '</a>' + "</th>";
            rowHTML += "<th>" + '<a href = "movies.html?title=' + String.fromCharCode(42) + '&sort=r_dsc_t_asc&pagenum=25&offset=0">' + String.fromCharCode(42).toUpperCase() + '</a>' + "</th>";
            rowHTML += "</tr>";
            genreTitleTableBody.append(rowHTML);
            break;
        }
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href = "movies.html?title=' + String.fromCharCode(m) + '&sort=r_dsc_t_asc&pagenum=25&offset=0">' + String.fromCharCode(m).toUpperCase() + '</a>' + "</th>";
        m++;
        rowHTML += "<th>" + '<a href = "movies.html?title=' + String.fromCharCode(m) + '&sort=r_dsc_t_asc&pagenum=25&offset=0">' + String.fromCharCode(m).toUpperCase() + '</a>' + "</th>";
        m++;
        rowHTML += "<th>" + '<a href = "movies.html?title=' + String.fromCharCode(m) + '&sort=r_dsc_t_asc&pagenum=25&offset=0">' + String.fromCharCode(m).toUpperCase() + '</a>' + "</th>";
        m++;
        rowHTML += "<th>" + '<a href = "movies.html?title=' + String.fromCharCode(m) + '&sort=r_dsc_t_asc&pagenum=25&offset=0">' + String.fromCharCode(m).toUpperCase() + '</a>' + "</th>";
        m++;
        rowHTML += "<th>" + '<a href = "movies.html?title=' + String.fromCharCode(m) + '&sort=r_dsc_t_asc&pagenum=25&offset=0">' + String.fromCharCode(m).toUpperCase() + '</a>' + "</th>";
        rowHTML += "</tr>";
        genreTitleTableBody.append(rowHTML);
    }

}

function getUrl()
{
    var search = document.getElementById("search").value;

    let url = "movies.html?";
    url += "search=";
    url += search;
    url += "&sort=r_dsc_t_asc&pagenum=25&offset=0";
    window.location.href = url;
}

function getmultiUrl(){
    var mtitle = document.getElementById("mtitle").value;
    var year = document.getElementById("year").value;
    var director = document.getElementById("director").value;
    var sname = document.getElementById("sname").value;

    let url = "movies.html?";
    url += "mtitle=";
    url += mtitle;
    url += "&year=";
    url += year;
    url += "&director=";
    url += director;
    url += "&sname=";
    url += sname;
    url += "&sort=r_dsc_t_asc&pagenum=25&offset=0";
    console.log(url);
    window.location.href = url;
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/genre", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleBrowseResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});