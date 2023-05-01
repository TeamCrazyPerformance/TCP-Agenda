import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import Layout from 'components/Layout';
import Create from 'pages/Create';
import Detail from 'pages/Detail';
import Home from 'pages/Home';
import Login from 'pages/Login';

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/Login" element={<Login />} />
          <Route path="/Create" element={<Create />} />
          <Route path="/Detail" element={<Detail />} />
          <Route path="*" element={<Home />} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App;
