import "./App.css";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Login from "./pages/login";

import AppLayout from "./pages/app/AppLayout";

import Dashboard from "./pages/app/Dashboard";
import Activity from "./pages/app/Activity";
import Users from "./pages/app/Users";
import Services from "./pages/app/Services";
import Security from "./pages/app/Security";
import Settings from "./pages/app/Settings";
import Information from "./pages/app/Information";
import Categories from "./pages/app/Categories";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />

        <Route path="/app" element={<AppLayout />}>
          <Route index element={<Dashboard />} />

          <Route path="activity" element={<Activity />} />
          <Route path="users" element={<Users />} />
          <Route path="services" element={<Services />} />
          <Route path="security" element={<Security />} />
          <Route path="settings" element={<Settings />}>
            <Route index element={<Navigate to="information" replace />} />

            <Route path="information" element={<Information />} />
            <Route path="categories" element={<Categories />} />
          </Route>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;