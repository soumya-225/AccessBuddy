function optimizeContrast(issues) {
  issues.forEach(issue => {
    if (issue.code == "WCAG2AA.Principle1.Guideline1_4.1_4_3.G18.Fail") {
      const element = document.querySelector(issue.selector);
      const hexColor = issue.message.match(/#[a-fA-F0-9]{6}/)?.[0];
      if (issue.message.includes("text colour")) {
        element.style.color = hexColor;
      } else {
        element.style.backgroundColor = hexColor;
      }
    }
  });
}

function getImageCaption(url) {
  fetch("http://localhost:3000/caption", {
    method: "POST",
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      url: url
    })
  }).then(res => {
    if (res.ok)
      return res.text();
    throw new Error("Failed to fetch");
  })
    .catch(() => {
      console.log("Error");
      return "Error";
    });
}

function optimizeImageAlt() {
  const images = document.querySelectorAll("img");
  // images.forEach((image) => {
  //   if (image.alt.trim() == "") {
  //     console.log(image);
  //     console.log(getImageCaption(image.src));
  //   }
  // });
  console.log(getImageCaption(images[0].src));
}


fetch("http://localhost:3000/analyze", {
  method: "POST",
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    url: document.URL
  })
}).then(res => {
  if (res.ok)
    return res.json();
  throw new Error("Failed to fetch");
})
  .then(json => {
    console.log(json);
    optimizeContrast(json.issues);
  })
  .catch(() => {
    console.log("Error");
  });

optimizeImageAlt();