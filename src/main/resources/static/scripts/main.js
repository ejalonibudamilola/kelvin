var card1 = {
    type: 'bar',
    data: {
        labels: xList,
        datasets: [{
                label: 'Promoted Staffs Dataset',
                fill: false,
//            backgroundColor: 'rgb(255, 99, 132)',
//            borderColor: 'rgb(255, 99, 132)',
                backgroundColor: ["rgba(255, 99, 132, 0.5)", "rgba(255, 159, 64, 0.5)",
                    "rgba(255, 205, 86, 0.5)", "rgba(75, 192, 192, 0.5)",
                    "rgba(54, 162, 235, 0.5)"],
                borderColor: ["rgb(255, 99, 132)", "rgb(255, 159, 64)", "rgb(255, 205, 86)",
                    "rgb(75, 192, 192)", "rgb(54, 162, 235)"],
                borderColor: "#c45850",
                data: yList
            }]
    },
    options: {
        scales: {
            yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'Total Number of Staff Promoted'
                    },
                    ticks: {
                        beginAtZero: true,
                        precision: 0
                    }
                }],
            xAxes: [{
                    scaleLabel: {
                        display: true
//                        labelString: 'MDAs'
                    },
                    ticks: {
                        maxTicksLimit: 5
                    },
                    barPercentage: 0.5
                }]
        },
        title: {
            display: true,
            text: 'Promoted Staffs'
        },
        plugins: {
           datalabels: {
             display: false
           }
        },
        onClick: handleBar1Click
    }
};
var card1Chart;

$('#cch1').on('change', function () {
    var chk = $(this);
    var cchv = chk.val();
    console.log("cch value is " + cchv);
    exchange1(cchv);
});
var card1Ctx = document.getElementById("barChart1").getContext("2d");
card1Chart = new Chart(card1Ctx, card1);
function exchange1(newType) {
    // Remove the old chart and all its event handles
    if (card1Chart != null) {
        card1Chart.destroy();
    }
    // Chart.js modifies the object you pass in. Pass a copy of the object so we can use the original object later
    var temp = jQuery.extend(card1);
    temp.type = newType;
    card1Chart = new Chart(card1Ctx, temp);
}
var card2 = {
    type: 'bar',
    data: {
        labels: x2List,
        datasets: [{
                label: 'New Staff Dataset',
                fill: false,
//            backgroundColor: 'rgb(255, 99, 132)',
//            borderColor: 'rgb(255, 99, 132)',
                backgroundColor: ["rgba(142, 94, 162, 0.6)", "rgba(212, 172, 13, 0.6)", "rgba(60, 186, 159, 0.8)",
                                    "rgba(232, 195, 185, 0.8)", "rgba(100, 30, 22, 0.6)"],
                borderColor: ["rgba(142, 94, 162)", "rgba(212, 172, 13)", "rgba(60, 186, 159)",
                                    "rgba(232, 195, 185)", "rgba(100, 30, 22)"],
                borderColor: "#c45850",
                data: y2List
            }]
    },
    options: {
        scales: {
            yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'Total Number of Staff Recruited'
                    },
                    ticks: {
                        beginAtZero: true,
                        precision: 0
                    }
                }],
            xAxes: [{
                    scaleLabel: {
                        display: true
//                        labelString: 'MDAs'
                    },
                    ticks: {
                        maxTicksLimit: 5
                    },
                    barPercentage: 0.5
                }]
        },
        title: {
            display: true,
            text: 'Newly Recruited Staffs'
        },
        plugins: {
           datalabels: {
              display: false
           }
        },
        onClick: handleBar2Click
    }
};

var card2Chart;

$('#cch2').on('change', function () {
    var chk = $(this);
    var cchv = chk.val();
    console.log("cch value is " + cchv);
    exchange2(cchv);
});
var card2Ctx = document.getElementById("barChart2").getContext("2d");
card2Chart = new Chart(card2Ctx, card2);
function exchange2(newType) {
    // Remove the old chart and all its event handles
    if (card2Chart != null) {
        card2Chart.destroy();
    }
    // Chart.js modifies the object you pass in. Pass a copy of the object so we can use the original object later
    var temp = jQuery.extend(card2);
    temp.type = newType;
    card2Chart = new Chart(card2Ctx, temp);
}

 var card3 ={
     type: 'pie',
     data: {
         labels: pieLabel,
         datasets: [{
                 data: pieData,
                 backgroundColor: ["rgba(255, 0, 0, 0.5)", "rgba(142, 99, 167, 0.8)", "rgba(60, 186, 159, 0.8)",
                     "rgba(200, 195, 180, 0.8)", "rgba(150, 75, 0, 0.5)", "rgba(245, 200, 0, 0.5)", "rgba(85, 0, 160, 0.5)"]
             }]
     },
    options: {
         title: {
             display: true,
             text: 'MDA (Ministries, Departments and Agencies) Staffs'
         },
         plugins: {
                  datalabels: {
                          // render 'label', 'value', 'percentage', 'image' or custom function, default is 'percentage'
                          mode: 'percentage',
//                          showZero: true,
                          color: '#fff',
                          display: function(context) {
                                return context.dataset.data[context.dataIndex] > 0; // or >= 1 or ...
                          }
                  }
         },
         onClick: handlePieClick
     }
 };


 var card3Chart;

 $('#cch3').on('change', function () {
     var chk = $(this);
     var cchv = chk.val();
     console.log("cch3 value is " + cchv);
     exchange3(cchv);
 });
 var card3Ctx = document.getElementById("pieChart1").getContext("2d");
 card3Chart = new Chart(card3Ctx, card3);
 function exchange3(newType) {
     // Remove the old chart and all its event handles
     if (card3Chart != null) {
         card3Chart.destroy();
     }
     // Chart.js modifies the object you pass in. Pass a copy of the object so we can use the original object later
     var temp2 = jQuery.extend(card3);
     temp2.type = newType;
     console.log("new type is "+ temp2.type);
     card3Chart = new Chart(card3Ctx, temp2);
     console.log(card3Chart);
 }



var card4 ={
    type: 'pie',
    data: {
        labels: pieLabel2,
        datasets: [{
                label: "MDAs Dataset",
//            backgroundColor: ["#3e95cd", "#8e5ea2","#3cba9f","#e8c3b9","#c45850"],
                backgroundColor: ["rgba(62, 149, 205, 0.8)", "rgba(142, 94, 162, 0.8)", "rgba(60, 186, 159, 0.8)",
                    "rgba(232, 195, 185, 0.8)", "rgba(196, 203, 207, 0.8)"],
                data: pieData2
            }]
    },
    options: {
        title: {
            display: true,
            text: 'Special Allowance Information'
        },
         plugins: {
            datalabels: {
               formatter: (value) => {
                return (value + "%")
               },
               color: '#fff',
               display: function(context) {
                            return context.dataset.data[context.dataIndex] > 0; // or >= 1 or ...
               }
            }
         },
        onClick: handlePie2Click
    }
};

var card4Chart;

$('#cch4').on('change', function () {
    var chk = $(this);
    var cchv = chk.val();
    console.log("cch value is " + cchv);
    exchange4(cchv);
});
var card4Ctx = document.getElementById("pieChart2").getContext("2d");
card4Chart = new Chart(card4Ctx, card4);
function exchange4(newType) {
    // Remove the old chart and all its event handles
    if (card4Chart != null) {
        card4Chart.destroy();
    }
    // Chart.js modifies the object you pass in. Pass a copy of the object so we can use the original object later
    var temp = jQuery.extend(card4);
    temp.type = newType;
    console.log("new type is "+ temp.type);
    if(temp.type === 'doughnut'){
    card4Chart = new Chart(card4Ctx, temp).Doughnut();
    console.log(card4Chart);
    }
    else{
        card4Chart = new Chart(card4Ctx, temp);
    }
}

var card5 ={
    type: 'pie',
    data: {
        labels: pieLabel3,
        datasets: [{
                data: pieData3,
                backgroundColor: ["rgba(255, 0, 0, 0.5)", "rgba(142, 99, 167, 0.8)", "rgba(60, 186, 159, 0.8)",
                    "rgba(200, 195, 180, 0.8)", "rgba(150, 75, 0, 0.5)", "rgba(245, 0, 150, 0.5)", "rgba(159, 99, 200, 0.5)"]
            }]
    },
   options: {
        title: {
            display: true,
            text: 'Paycheck Information'
        },
        plugins: {
            datalabels: {
                formatter: (value) => {
                    return (value + "%")
                },
                color: '#fff',
                display: function(context) {
                    return context.dataset.data[context.dataIndex] > 0; // or >= 1 or ...
                }
            }
        },
        onClick: handlePie3Click
    }
};


var card5Chart;

$('#cch5').on('change', function () {
    var chk = $(this);
    var cchv = chk.val();
    console.log("cch5 value is " + cchv);
    exchange5(cchv);
});
var card5Ctx = document.getElementById("pieChart3").getContext("2d");
card5Chart = new Chart(card5Ctx, card5);
function exchange5(newType) {
    // Remove the old chart and all its event handles
    if (card5Chart != null) {
        card5Chart.destroy();
    }
    // Chart.js modifies the object you pass in. Pass a copy of the object so we can use the original object later
     var temp2 = jQuery.extend(card5);
     temp2.type = newType;
}


var card6 = {
    type: 'bar',
    data: {
        labels: x3List,
        datasets: [{
                label: 'Absorbed Staffs Dataset',
                backgroundColor: ["rgba(255, 99, 132, 0.5)", "rgba(255, 159, 64, 0.5)",
                    "rgba(255, 205, 86, 0.5)", "rgba(75, 192, 192, 0.5)",
                    "rgba(54, 162, 235, 0.5)"],
                borderColor: ["rgb(255, 99, 132)", "rgb(255, 159, 64)", "rgb(255, 205, 86)",
                    "rgb(75, 192, 192)", "rgb(54, 162, 235)"],
                borderColor: "#c45850",
                data: y3List
            }]
    },
    options: {
        scales: {
            yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'Total Number of Staff Absorbed'
                    },
                    ticks: {
                        beginAtZero: true,
                        precision: 0
                    }
                }],
            xAxes: [{
                    scaleLabel: {
                        display: true
//                        labelString: 'MDAs'
                    },
                    ticks: {
                        maxTicksLimit: 5
                    },
                    barPercentage: 0.5
                }]
        },
        title: {
            display: true,
            text: 'Absorbed Staffs'
        },
        plugins: {
           datalabels: {
              display: false
           }
        },
        onClick: handleBar6Click
    }
};

var card6Chart;

$('#cch6').on('change', function () {
    var chk = $(this);
    var cchv = chk.val();
    console.log("cch value is " + cchv);
    exchange6(cchv);
});
var card6Ctx = document.getElementById("barChart3").getContext("2d");
card6Chart = new Chart(card6Ctx, card6);
function exchange6(newType) {
    // Remove the old chart and all its event handles
    if (card6Chart != null) {
        card6Chart.destroy();
    }
    // Chart.js modifies the object you pass in. Pass a copy of the object so we can use the original object later
    var temp = jQuery.extend(card6);
    temp.type = newType;
    card6Chart = new Chart(card6Ctx, temp);
}

var card7 = {
    type: 'bar',
    data: {
        labels: x4List,
        datasets: [{
                label: 'Reinstated Staffs Dataset',
                backgroundColor: ["rgba(255, 99, 132, 0.5)", "rgba(255, 159, 64, 0.5)",
                    "rgba(255, 205, 86, 0.5)", "rgba(75, 192, 192, 0.5)",
                    "rgba(54, 162, 235, 0.5)"],
                borderColor: ["rgb(255, 99, 132)", "rgb(255, 159, 64)", "rgb(255, 205, 86)",
                    "rgb(75, 192, 192)", "rgb(54, 162, 235)"],
                borderColor: "#c45850",
                data: y4List
            }]
    },
    options: {
        scales: {
            yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'Total Number of Staff Reinstated'
                    },
                    ticks: {
                        beginAtZero: true,
                        precision: 0
                    }
                }],
            xAxes: [{
                    scaleLabel: {
                        display: true
//                        labelString: 'MDAs'
                    },
                    ticks: {
                        maxTicksLimit: 5
                    },
                    barPercentage: 0.5
                }]
        },
        title: {
            display: true,
            text: 'Reinstated Staffs'
        },

        plugins: {
           datalabels: {
              display: false
           }
        },
        onClick: handleBar7Click
    }
};

var card7Chart;

$('#cch7').on('change', function () {
    var chk = $(this);
    var cchv = chk.val();
    console.log("cch value is " + cchv);
    exchange7(cchv);
});
var card7Ctx = document.getElementById("barChart4").getContext("2d");
card7Chart = new Chart(card7Ctx, card7);
function exchange7(newType) {
    // Remove the old chart and all its event handles
    if (card7Chart != null) {
        card7Chart.destroy();
    }
    // Chart.js modifies the object you pass in. Pass a copy of the object so we can use the original object later
    var temp = jQuery.extend(card7);
    temp.type = newType;
    card7Chart = new Chart(card7Ctx, temp);
}


var chartLabel;
var url;
console.log("are you on top of this function");
function handleBar1Click(evt) {
    console.log("I am in bar");
    chartLabel = card1Chart.getElementsAtEvent(evt);
    url = card1Url;
    handleClick2(chartLabel, url);
}
function handleBar2Click(evt) {
    console.log("I am in bar");
    chartLabel = card2Chart.getElementsAtEvent(evt);
    url = card2Url;
    handleClick2(chartLabel, url);
}
function handlePieClick(evt) {
    console.log("I am in pie");
    chartLabel = card3Chart.getElementsAtEvent(evt);
    url = card3Url;
    handleClick2(chartLabel, url);
}

function handlePie2Click(evt) {
    console.log("I am in pie");
    chartLabel = card4Chart.getElementsAtEvent(evt);
    url = card4Url;
    handleClick2(chartLabel, url);
}

function handlePie3Click(evt) {
    console.log("I am in pie");
    chartLabel = card5Chart.getElementsAtEvent(evt);
    url = card5Url;
    handleClick2(chartLabel, url);
}

function handleBar6Click(evt) {
    console.log("I am in bar");
    chartLabel = card6Chart.getElementsAtEvent(evt);
    url = card6Url;
    handleClick2(chartLabel, url);
}

function handleBar7Click(evt) {
    console.log("I am in bar");
    chartLabel = card7Chart.getElementsAtEvent(evt);
    url = card7Url;
    handleClick2(chartLabel, url);
}

function handleClick2(chartValue, url) {
    $(".spin").show();
    console.log("url here is "+url);
    console.log("chartLabel is " + chartValue);
    console.log("rm and ry are "+rm +" "+ry);
    if (chartValue[0]) {
        var chartData = chartValue[0]['_chart'].config.data;
        var idx = chartValue[0]['_index'];

        var label = chartData.labels[idx];
        var value = chartData.datasets[0].data[idx];
        var w = url+"?label="+label+"&rm="+rm+"&ry="+ry+"";
        console.log("w is "+w);
        $.ajax({
            type: "GET",
            url: url,
            data: {
                label: label,
                rm: rm,
                ry: ry
            },

            success: function (response) {
                // do something ...
                console.log("Ajax call successful " + response);
                console.log("Label for this bar is " + label);
                $('#result').html(response);
            },
            error: function (e) {
                alert('Error: ' + e);
            }
        });
    }
}