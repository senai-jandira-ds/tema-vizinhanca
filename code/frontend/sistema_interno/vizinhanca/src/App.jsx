import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./pages/login";
import System from "./pages/system";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/app" element={<System />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;