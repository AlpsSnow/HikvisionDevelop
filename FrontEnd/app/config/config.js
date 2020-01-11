export default {
    singular: true,
    plugins: [
        ['umi-plugin-react', {
            antd: true  
        }],    
      ],
    routes: [{
        path: '/',    
        component: './HelloWorld',    
      }],
      
   proxy: {
     '/dev': {
       target: 'http://localhost:8080',
       changeOrigin: true,
       "pathRewrite": { "^/dev" : "" }
      },
    },
};