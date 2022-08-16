

// const pie_chart = d3.select('#davis svg')
//                 .attr('id','pie-chart')
//                 .attr('height',600)
//                 .attr('width',600)
//                 .attr('transform','translate(300,300)');

// console.log(pie_chart)     
// // const pie = d3.pie().value(d => d);
// // const arc = d3.arc()
// //               .outerRadius(150)
// //               .innerRadius(90);

// // const data = [3,6,9];              
// // const colorScale = d3.scaleOrdinal(['yellow','white','green']);
// // const g = svg.selectAll('.arc')
// //              .data(pie(data))    
// //              .enter().append('g')
// //              .attr('class','arc')
// // g.append('path')
// //  .attr('d',arc)
// //  .attr('class','arc')
// //  .style('fill',(d,i) => colorScale(i))
// //  .style('stroke','black')
// //  .style('stroke-width',4);
// async function wait_for_empdata()
// {
//     while(!emp_data)
//     {
//       await sleep(60);
//       console.log('loooop');
//     }
    
//     davis();
// }


// async function davis()
// {
   
//     console.log('no davis');
//     var worked_time = 0;var work_days = [];
//     const curr_month = `${calender.year}-${calender.month}`;
//     console.log(curr_month);
//     for(key in emp_data)
//     {
//       const obj = emp_data[key];
//       console.log(obj);
//       if( obj.check_in.substr(0,7) == curr_month)
//       {
//         // var check_in_date,check_in_time,check_out_date,check_out_time;
//         var check_in_date = obj.check_in.substr(8,2);
//         // try{check_out_date = obj.check_out.substr(8,2);}catch{};
//         // try{check_in_time = obj.check_in.substr(11);}catch{};
//         // try{check_out_time = obj.check_out.substr(11);}catch{};
//         if(!work_days.includes(check_in_date))
//         {
//             work_days.push(check_in_date);
//             console.log('work_days',work_days.length,typeof work_days.length);
//         }
//         let check_in = new Date(obj.check_in.substr(0,10)+' '+obj.check_in.substr(11));
//         let check_out,time_interval;
//         try{
//             check_out = new Date(obj.check_out.substr(0,10)+' '+obj.check_out.substr(11));
//             time_interval = (check_out - check_in);
//         }
//         catch{
//             time_interval = 9*1000*60*60;
//         }; 
//         console.log('in-out',check_in,check_out);
//         // console.log(check_in_date,check_out_date,check_in_time,check_out_time);
//         worked_time += time_interval;
//       }
//     }

//     chart = DonutChart([
//         { name:'present',value:work_days.length},
//         {name:'absent',value:calender.getDaysnum().working-work_days.length},
//         {name:'sundays',value:calender.getDaysnum().sundays}
//     ], {
//     name: d => d.name,
//     value: d => d.value,
//     width:420,
//     height:420
//   });

//   await workTimeDisplay(Math.floor((worked_time)/100/60/60)/10);
// }     
// wait_for_empdata();
// document.querySelector('#right-cal-nav').addEventListener('click',(event) => {
//     calender.incMonth();
//     document.querySelector('#worked-time').remove();
//     davis();
//     event.stopPropagation();    
// });
// document.querySelector('#left-cal-nav').addEventListener('click',(event) => {
//     calender.decMonth();
//     document.querySelector('#worked-time').remove();
//     davis();
//     event.stopPropagation();
// });
// // function DonutChart(data, {
// //     name = ([x]) => x,  // given d in data, returns the (ordinal) label
// //     value = ([, y]) => y, // given d in data, returns the (quantitative) value
// //     title, // given d in data, returns the title text
// //     width = 450, // outer width, in pixels
// //     height = 450, // outer height, in pixels
// //     innerRadius = Math.min(width, height) / 3, // inner radius of pie, in pixels (non-zero for donut)
// //     outerRadius = Math.min(width, height) / 2, // outer radius of pie, in pixels
// //     labelRadius = (innerRadius + outerRadius) / 2, // center radius of labels
// //     format = ",", // a format specifier for values (in the label)
// //     names, // array of names (the domain of the color scale)
// //     colors, // array of colors for names
// //     stroke = innerRadius > 0 ? "none" : "white", // stroke separating widths
// //     strokeWidth = 1, // width of stroke separating wedges
// //     strokeLinejoin = "round", // line join of stroke separating wedges
// //     padAngle = stroke === "none" ? 1 / outerRadius : 0, // angular separation between wedges
// //   } = {}) {
// //     // Compute values.
// //     const N = d3.map(data, name);
// //     const V = d3.map(data, value);
// //     const I = d3.range(N.length).filter(i => !isNaN(V[i]));
  
// //     // Unique the names.
// //     if (names === undefined) names = N;
// //     names = new d3.InternSet(names);
  
// //     // Chose a default color scheme based on cardinality.
// //     if (colors === undefined) colors = d3.schemeSpectral[names.size];
// //     if (colors === undefined) colors = d3.quantize(t => d3.interpolateSpectral(t * 0.8 + 0.1), names.size);
  
// //     // Construct scales.
// //     const color = d3.scaleOrdinal(names, colors);
  
// //     // Compute titles.
// //     if (title === undefined) {
// //       const formatValue = d3.format(format);
// //       title = i => `${N[i]}\n${formatValue(V[i])}`;
// //     } else {
// //       const O = d3.map(data, d => d);
// //       const T = title;
// //       title = i => T(O[i], i, data);
// //     }
  
// //     // Construct arcs.
// //     const arcs = d3.pie().padAngle(padAngle).sort(null).value(i => V[i])(I);
// //     const arc = d3.arc().innerRadius(innerRadius).outerRadius(outerRadius);
// //     const arcLabel = d3.arc().innerRadius(labelRadius).outerRadius(labelRadius);
    
// //     const svg = d3.select("#pie-chart")
// //         .attr("width", width)
// //         .attr("height", height)
// //         .attr("viewBox", [-width / 2, -height / 2, width, height])
// //         .attr("style", "max-width: 100%; height: auto; height: intrinsic;");
  
// //     svg.append("g")
// //         .attr("stroke", stroke)
// //         .attr("stroke-width", strokeWidth)
// //         .attr("stroke-linejoin", strokeLinejoin)
// //       .selectAll("path")
// //       .data(arcs)
// //       .join("path")
// //         .attr("fill", d => color(N[d.data]))
// //         .attr("d", arc)
// //       .append("title")
// //         .text(d => title(d.data));
  
// //     svg.append("g")
// //         .attr("font-family", "sans-serif")
// //         .attr("font-size", 10)
// //         .attr("text-anchor", "middle")
// //       .selectAll("text")
// //       .data(arcs)
// //       .join("text")
// //         .attr("transform", d => `translate(${arcLabel.centroid(d)})`)
// //       .selectAll("tspan")
// //       .data(d => {
// //         const lines = `${title(d.data)}`.split(/\n/);
// //         return (d.endAngle - d.startAngle) > 0.25 ? lines : lines.slice(0, 1);
// //       })
// //       .join("tspan")
// //         .attr("x", 0)
// //         .attr("y", (_, i) => `${i * 1.1}em`)
// //         .attr("font-weight", (_, i) => i ? null : "bold")
// //         .text(d => d);
  
// //     return Object.assign(svg.node(), {scales: {color}});
// // }  
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
//     if (colors === undefined) colors = ['green','red','yellow'];
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
    
//     const svg = d3.select("#pie-chart")
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
// async function workTimeDisplay(work_time)
// {
//     let worked_time = d3.select('#davis')
//                               .append('svg')
//                               .attr('id','worked-time')
//                               .attr('height',420)
//                               .attr('width',420)
//                               .append('g');

//         let arc = d3.arc()
//               .innerRadius(177)
//               .outerRadius(180)
//               .startAngle(0)
//               .endAngle((2*Math.PI));
        
//         let arc_path = worked_time.append("path")
//           .attr('transform','translate(210,210)')
//           .attr("id", "arc")
//           .attr("d", arc)
//           .attr("fill","green");   
        
//         let text = worked_time.append('text')
//           .attr('id','w_time')
//           .style('fill','green')
//           .style('font-size','3rem')
//           .text(`${Math.floor(work_time)} hrs`);
        
//         let text_dim_info = document.querySelector('#w_time').getBoundingClientRect();
//         let svg_dim = document.querySelector('#worked-time').getBoundingClientRect();
//         let text_x = (svg_dim.width - text_dim_info.width)/2;
//         let text_y = svg_dim.height/2;
        
//         text.attr('x',text_x)
//             .attr('y',text_y);

//     for(let i = 0;i <= 100;i++)
//      {
//         //update
//         console.log('not update')
//          arc.endAngle((2*Math.PI)*(i/100));   
//          arc_path.attr('d',arc)
//          text.text(`${Math.floor(work_time*(i/100))} hrs`);
        
//         text_dim_info = document.querySelector('#w_time').getBoundingClientRect();
//         svg_dim = document.querySelector('#worked-time').getBoundingClientRect();
//         text_x = (svg_dim.width - text_dim_info.width)/2;
//         text_y = svg_dim.height/2;
        
//         text.attr('x',text_x)
//             .attr('y',text_y);

//         //wait
//         await sleep(15);

//      }     
     
//       //Create an SVG text element and append a textPath element
//       // svg.append("text")
//       // .append("textPath") //append a textPath to the text element
//       // .attr("xlink:href", "#wavy") //place the ID of the path here
//       // .style("text-anchor","middle") //place the text halfway on the arc
//       // .attr("startOffset", "50%")
//       // .text("Yay,class my text is on a wavy path");
// }


function davis(raw_data)
{
   
}