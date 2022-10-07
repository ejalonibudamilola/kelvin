var scard = {
    type: 'bar',
    data: {
        labels: xList,
        datasets: [{
                label: labelForCD,
                fill: false,
            backgroundColor: 'rgb(255, 99, 132)',
            borderColor: 'rgb(255, 99, 132)',
//                backgroundColor: ["rgba(255, 99, 132, 0.5)", "rgba(255, 159, 64, 0.5)",
//                    "rgba(255, 205, 86, 0.5)", "rgba(75, 192, 192, 0.5)",
//                    "rgba(54, 162, 235, 0.5)"],
//                borderColor: ["rgb(255, 99, 132)", "rgb(255, 159, 64)", "rgb(255, 205, 86)",
//                    "rgb(75, 192, 192)", "rgb(54, 162, 235)"],
//                borderColor: "#c45850",
                data: yList
            }]
    },
    options: {
        scales: {
            yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: ylabel
                    },
                    ticks: {
                        beginAtZero: true,
                        precision: 0
                    }
                }],
            xAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'MDAs'
                    },
                    ticks: {
                        maxTicksLimit: 5,
                        autoSkip: false
                    },
                    barPercentage: 0.5
                }]
        },
        title: {
            display: true,
            text: chartTitle
        },
        plugins: {
            datalabels: {
               display: false
            }
        },
        onClick: handleSBarClick
    }
};
var scardChart;

$('#scch').on('change', function () {
    var chk = $(this);
    var cchv = chk.val();
    console.log("cch value is " + cchv);
    exchange1(cchv);
});
var scardCtx = document.getElementById("sChart").getContext("2d");
scardChart = new Chart(scardCtx, scard);
function exchange1(newType) {
    // Remove the old chart and all its event handles
    if (scardChart) {
        scardChart.destroy();
    }
    // Chart.js modifies the object you pass in. Pass a copy of the object so we can use the original object later
    var temp = jQuery.extend(scard);
    temp.type = newType;
    scardChart = new Chart(scardCtx, temp);
}


var chartLabel;
console.log("are you on top of this function");

function handleSBarClick(evt) {
    console.log("I am in bar");
    chartLabel = scardChart.getElementsAtEvent(evt);
    handleClick2(chartLabel, surl);
}


function handleClick2(chartValue, surl) {
    $(".spinSC").show();
    console.log("url here is "+surl);
    console.log("chartLabel is " + chartValue);
    console.log("rm and ry are "+srm +" "+sry);
    if (chartValue[0]) {
        var chartData = chartValue[0]['_chart'].config.data;
        var idx = chartValue[0]['_index'];

        var label = chartData.labels[idx];
        var value = chartData.datasets[0].data[idx];
        console.log("before ajax call in single chart view");
        $.ajax({
            type: "GET",
            url: surl,
            data: {
                label: label,
                rm: srm,
                ry: sry
            },
            success: function (response) {
                // do something ...
                console.log("Ajax call successful " + response);
                console.log("Label for this bar is " + label);
                $('#result1').html(response);
                $('#chartModal').modal('show');
                $(".spinSC").hide();
            },
            error: function (e) {
                alert('Error: ' + e);
            }
        });
    }
}


