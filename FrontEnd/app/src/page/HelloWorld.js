import React, { Component } from 'react';


import { 
    Button,
    Divider  
} from 'antd';

const bodystyle = { 
    margin: '30px',    
    boxShadow: '0 4px 8px 0 rgba(0, 0, 0, 0.2)',    
    border: '1px solid #e8e8e8',    
};

class Context extends React.Component {
  
  constructor(props){
    super(props)
    this.state = {
      name: '',
      base64data: '',
      isFinsh: false
    }
    this.handleClick=this.handleClick.bind(this)
  }

  handleClick(){
    this.setState({isFinsh: false})
    fetch('/dev/getImageName')
    .then(response=>response.json())
    .then(data=>{
      console.log(data.name)
      this.setState({
      name: data.name,
      base64data: data.base64data,
      isFinsh: true
    })})
    .catch(e=>{console.log('error: ' + e.toString())})
    }
    render(){
      let contextdiv
      let url = "http://localhost:8080/images/"
      if(this.state.isFinsh){
        contextdiv = <img src={url+this.state.name}/>
      }else{
        contextdiv = <p>采集中</p>
      }
       return (
        <div style={bodystyle}>
           <div>
              {contextdiv}
           </div>
           <Divider />
           <div>
              <Button type="primary" onClick={this.handleClick.bind(this)}>采集指纹图片</Button>
           </div>
           <Divider />
           <div>
              ccc
           </div>
        </div>
      );
    }
}
export default Context;