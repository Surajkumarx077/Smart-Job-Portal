import React from 'react';
import { Outlet } from 'react-router-dom';
import TopNav from './TopNav';

export default function AppShell() {
  return (
    <div className="app-layout">
      <TopNav />
      <main className="page-container">
        <Outlet />
      </main>
    </div>
  );
}
