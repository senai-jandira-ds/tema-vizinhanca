import { Link } from "react-router-dom";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { useState } from 'react'

import './App.css'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<div>Landing Page</div>} />
        <Route path="/login" element={<Login />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App
