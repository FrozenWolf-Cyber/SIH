

var emp_list;
$.ajax('https://sih-smart-attendance.herokuapp.com/get_user_overview', {
    type: 'POST',  
    success: function (data, status, xhr) {
        emp_list = JSON.parse(JSON.stringify(data));
    }
});

function sleep(ms) 
{
    return new Promise(resolve => setTimeout(resolve, ms));
}


const waitForData = async () => {
    while(!(emp_list))
    {
        console.log('fetching data ...');
        await sleep(60);
    }
    console.log('emp_list',emp_list);
    startWork();
}
waitForData();
function sqlToNosql(){
    var emp_list1 = [];
    
    for(let i = 0;i < (emp_list.emp_no.length);i++)
    {
        emp_list1[i] = {
            name:emp_list.name[i],
            gender:emp_list.gender[i],
            emp_no:emp_list.emp_no[i],
            designation:emp_list.designation[i],
            branch_name:emp_list.branch_name[i]
        }
    }

    console.log(emp_list1)
    return emp_list1;
}

function process_data()
{
   let data = {
      emp_num:{
        total:0,
        men:0
      },
      designation:{
           x:[],
           y:[]
      },
      branch:{
        branches:[],
        num:[]
      }
   };
   emp_list1.forEach(emp => {
      if(data.designation.x.includes(emp.designation))
      {
        data.designation.y[data.designation.x.indexOf(emp.designation)]++; 
      }
      else
      {
        data.designation.x.push(emp.designation);
        data.designation.y.push(1);
      }

      if(data.branch.branches.includes(emp.branch_name))
      {
        data.branch.num[data.branch.branches.indexOf(emp.branch_name)]++; 
      }
      else
      {
        data.branch.branches.push(emp.branch_name);
        data.branch.num.push(1);
      }
      
      data.emp_num.total += 1;
      if(emp.gender == 'Male' || emp.gender == 'M')
      {
        data.emp_num.men += 1;
      }
   });
   return data;
}

function davis(data)
{
  console.log('davis called');
  //gender
  Highcharts.chart('container1', {
    chart: {
      plotBackgroundColor: null,
      plotBorderWidth: null,
      plotShadow: false,
      type: 'pie'
    },
    title: {
      text: 'Gender Data'
    },
    tooltip: {
      pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
    },
    accessibility: {
      point: {
        valueSuffix: '%'
      }
    },
    plotOptions: {
      pie: {
        allowPointSelect: true,
        cursor: 'pointer',
        dataLabels: {
          enabled: true,
          format: '<b>{point.name}</b>: {point.percentage:.1f} %'
        }
      }
    },
    series: [{
      name: 'Gender',
      colorByPoint: true,
      data: [{
        name: 'Male',
        y: (data.emp_num.men/data.emp_num.total)*100,
        sliced: true,
        selected: true
      },{
        name:'Female',
        y:((data.emp_num.total - data.emp_num.men)/(data.emp_num.total))*100
      }]
    }]
  });
  

  //designation
  let desig_data = [];
  for(let i = 0;i < data.designation.x.length;i++)
  {
    let desig = {
      name:data.designation.x[i],
      y:(data.designation.y[i]/data.emp_num.total)*100
    };
    desig_data.push(desig);
  }
  console.log(desig_data);


  Highcharts.chart('container3', {
    chart: {
        type: 'column'
    },
    title: {
        align: 'center',
        text: 'Designation vs Employee number'
    },
    accessibility: {
        announceNewData: {
            enabled: true
        }
    },
    xAxis: {
        type: 'category'
    },
    yAxis: {
        title: {
            text: 'Number of Employee'
        }

    },
    legend: {
        enabled: false
    },
    plotOptions: {
        series: {
            borderWidth: 3,
            dataLabels: {
                enabled: true,
                format: '{point.y:.2f}%'
            }
        }
    },

    tooltip: {
        headerFormat: '<span style="font-size:12px">{series.name}</span><br>',
        pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>',
        
    }, 
    series:[
      {
        name: "Num of Emp",
        colorByPoint: true,
        data:desig_data
      }
    ]
    });
 

    console.log('davis data',data);  
  let branchs = [];
  console.log('data.branch',data.branch);
  const container6Data = [];
  for(let i = 0;i < data.branch.branches.length;i++)
  {
    container6Data.push({
      name:data.branch.branches[i],
      y:data.branch.num[i]
    });
  }
  // console.log(cont6Data',container6Data);
  Highcharts.chart('container6', {
    chart: {
      type: 'bar'
    },
    title: {
      text: 'Branch vs Number of Employees'
    },
    xAxis: {
      categories: data.branch.branches,
      title: {
        text: null
      }
    },
    yAxis: {
      min: 0,
      title: {
        text: 'Branch',
        align: 'high'
      },
      labels: {
        overflow: 'justify'
      }
    },
    plotOptions: {
      bar: {
        dataLabels: {
          enabled: true
        }
      }
    },
    legend: {
      layout: 'vertical',
      align: 'right',
      verticalAlign: 'top',
      x: -40,
      y: 80,
      floating: true,
      borderWidth: 1,
      backgroundColor:
        Highcharts.defaultOptions.legend.backgroundColor || '#FFFFFF',
      shadow: true
    },
    credits: {
      enabled: false
    },
    series: [{
      name:'Number of Employee',
      data:data.branch.num
    }]
  });


    const adv= document.querySelectorAll('.highcharts-credits');
    for(i of adv)
    {
      i.remove();
    }
}


var emp_list1;
function startWork()
{
    emp_list1 = sqlToNosql();
    const davis_data = process_data();
    davis(davis_data);
}
 
  

// async function waitForEmpList()
// {
//     while(!emp_list1)
//     {
//         await sleep(60);
//     }

//     renderDavis();
// }
// function DavisData()
// {

//     let num_of_men = 0,num_of_women = 0;
//     for(emp of emp_list1)
//     {
//         if(emp.gender == 'Male' || emp.gender == 'M')
//         {
//             num_of_men++;
//         }
//         else
//         {
//             num_of_women++;
//         }
//     }

//     let data = {
//         gender_data:{
//             men:num_of_men,
//             women:num_of_women
//         }
//     }

//     return data;
// }

// function renderDavis()
// {

//     const data = DavisData();
    // Highcharts.chart('container', {
    //   chart: {
    //     plotBackgroundColor: null,
    //     plotBorderWidth: null,
    //     plotShadow: false,
    //     type: 'pie'
    //   },
    //   title: {
    //     text: 'Gender Data'
    //   },
    //   tooltip: {
    //     pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
    //   },
    //   accessibility: {
    //     point: {
    //       valueSuffix: '%'
    //     }
    //   },
    //   plotOptions: {
    //     pie: {
    //       allowPointSelect: true,
    //       cursor: 'pointer',
    //       dataLabels: {
    //         enabled: true,
    //         format: '<b>{point.name}</b>: {point.percentage:.1f} %'
    //       }
    //     }
    //   },
    //   series: [{
    //     name: 'Brands',
    //     colorByPoint: true,
    //     data: [{
    //       name: 'Chrome',
    //       y: 61.41,
    //       sliced: true,
    //       selected: true
    //     }, {
    //       name: 'Sogou Explorer',
    //       y: 1.64
    //     }, {
    //       name: 'Opera',
    //       y: 1.6
    //     }, {
    //       name: 'QQ',
    //       y: 1.2
    //     }, {
    //       name: 'Other',
    //       y: 2.61
    //     }]
    //   }]
    // });
    //male-female chart
  //   DonutChart([
  //       { name:'male',value:data.gender_data.men},
  //       {name:'female',value:data.gender_data.women},
  //   ], {
  //   name: d => d.name,
  //   value: d => d.value,
  //   width:240,
  //   height:240
  // });   
// }

// function DonutChart(data, {
//     name = ([x]) => x,  // given d in data, returns the (ordinal) label
//     value = ([, y]) => y, // given d in data, returns the (quantitative) value
//     title, // given d in data, returns the title text
//     width = 640, // outer width, in pixels
//     height = 400, // outer height, in pixels
//     innerRadius = Math.min(width, height) / 3, // inner radius of pie, in pixels (non-zero for donut)
//     outerRadius = Math.min(width, height) / 2, // outer radius of pie, in pixels
//     labelRadius = (innerRadius + outerRadius) / 2, // center radius of labels
//     format = ",", // a format specifier for values (in the label)
//     names, // array of names (the domain of the color scale)
//     colors, // array of colors for names
//     stroke = innerRadius > 0 ? "none" : "white", // stroke separating widths
//     strokeWidth = 1, // width of stroke separating wedges
//     strokeLinejoin = "round", // line join of stroke separating wedges
//     padAngle = stroke === "none" ? 1 / outerRadius : 0, // angular separation between wedges
//   } = {}) {
//     // Compute values.
//     const N = d3.map(data, name);
//     const V = d3.map(data, value);
//     const I = d3.range(N.length).filter(i => !isNaN(V[i]));
  
//     // Unique the names.
//     if (names === undefined) names = N;
//     names = new d3.InternSet(names);
  
//     // Chose a default color scheme based on cardinality.
//     if (colors === undefined) colors = ['blue','red'];
//     if (colors === undefined) colors = d3.quantize(t => d3.interpolateSpectral(t * 0.8 + 0.1), names.size);
  
//     // Construct scales.
//     const color = d3.scaleOrdinal(names, colors);
  
//     // Compute titles.
//     if (title === undefined) {
//       const formatValue = d3.format(format);
//       title = i => `${N[i]}\n${formatValue(V[i])}`;
//     } else {
//       const O = d3.map(data, d => d);
//       const T = title;
//       title = i => T(O[i], i, data);
//     }
  
//     // Construct arcs.
//     const arcs = d3.pie().padAngle(padAngle).sort(null).value(i => V[i])(I);
//     const arc = d3.arc().innerRadius(innerRadius).outerRadius(outerRadius);
//     const arcLabel = d3.arc().innerRadius(labelRadius).outerRadius(labelRadius);
    
//     var svg = d3.select('#gender')
//         .attr("width", width)
//         .attr("height", height)
//         .attr("viewBox", [-width / 2, -height / 2, width, height])
//         .attr("style", "max-width: 100%; height: auto; height: intrinsic;");
  
//     svg.append("g")
//         .attr("stroke", stroke)
//         .attr("stroke-width", strokeWidth)
//         .attr("stroke-linejoin", strokeLinejoin)
//       .selectAll("path")
//       .data(arcs)
//       .join("path")
//         .attr("fill", d => color(N[d.data]))
//         .attr("d", arc)
//       .append("title")
//         .text(d => title(d.data));
  
//     svg.append("g")
//         .attr("font-family", "sans-serif")
//         .attr("font-size", 10)
//         .attr("text-anchor", "middle")
//       .selectAll("text")
//       .data(arcs)
//       .join("text")
//         .attr("transform", d => `translate(${arcLabel.centroid(d)})`)
//       .selectAll("tspan")
//       .data(d => {
//         const lines = `${title(d.data)}`.split(/\n/);
//         return (d.endAngle - d.startAngle) > 0.25 ? lines : lines.slice(0, 1);
//       })
//       .join("tspan")
//         .attr("x", 0)
//         .attr("y", (_, i) => `${i * 1.1}em`)
//         .attr("font-weight", (_, i) => i ? null : "bold")
//         .text(d => d);
  
// }
// waitForEmpList();