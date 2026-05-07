import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import Login from "./pages/login";
import Activity from "./pages/app/Activity";
import AppLayout from "./pages/app/AppLayout";
import Dashboard from "./pages/app/Dashboard";
import Users from "./pages/app/Users";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />

        {/* Área logada */}
        <Route path="/app" element={<AppLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="activity" element={<Activity />} />
          <Route path="users" element={<Users />} />
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