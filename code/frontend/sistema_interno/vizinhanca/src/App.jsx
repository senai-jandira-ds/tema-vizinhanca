import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import Login from "./pages/login";
import SystemLayout from "./pages/system/layout";
import Dashboard from "./pages/system/home";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/app" element={<SystemLayout />}>
          <Route index element={<Dashboard />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;