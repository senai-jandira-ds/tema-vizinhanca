import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import Login from "./pages/login";
import Activity from "./pages/app/Activity";
import AppLayout from "./pages/app/AppLayout";
import Dashboard from "./pages/app/Dashboard";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />

        {/* Área logada */}
        <Route path="/app" element={<AppLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="activity" element={<Activity />} />
        </Route>
        {/*<Route path="users" element={<Users />} />
          <Route path="services" element={<Services />} />
          <Route path="security" element={<Security />} />
          <Route path="settings" element={<Settings />} />
          <Route path="help" element={<Help />} />
*/}
    </Routes>
    </BrowserRouter >
  );
}

export default App;