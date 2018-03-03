const normalize = require('normalize-svg-coords');
const normalizedPath = normalize({
  viewBox: '0 0 400 460',
  path: 'm164.94444,120.72222c0,0 -67.18345,98.76819 -38,119.00001c38.13301,26.43622 125,-52 131.00002,-49c47.35185,16.66667 122.70369,43.33333 170.05554,60',
  min: 0,
  max: 10,
  asList: true
})

console.log(normalizedPath); // M0.3772 0.3677c0.0277 -0.0019 0.0507 -0.0154 0.0611 -0.0375