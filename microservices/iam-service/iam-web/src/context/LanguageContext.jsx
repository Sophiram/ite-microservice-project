import React, { createContext, useContext, useState, useEffect } from 'react';

const translations = {
  en: {
    // Brand & Navigation
    appTitle: 'ISTAD IAM',
    adminBreadcrumb: 'Admin',
    dashboardBreadcrumb: 'Dashboard',
    accountPortal: 'User Portal',
    adminPortal: 'Admin Dashboard',
    switchPortal: 'Switch Portal',
    language: 'Language',
    
    // Live Dashboard Header
    liveMonitoringTag: 'Live System Monitoring',
    dashboardTitle: 'Operations & IAM Dashboard',
    dashboardSubtitle: 'Real-time overview of IAM identities, active tokens, microservices & authentication traffic.',
    refreshData: 'Refresh Data',
    lastUpdated: 'Updated just now',
    
    // 4 KPI Cards
    totalUsersTitle: 'Total Users',
    totalUsersSub: 'vs last month',
    activeSessionsTitle: 'Active Sessions',
    activeSessionsSub: 'Realtime active',
    rolesPermsTitle: 'Roles & Permissions',
    rolesPermsSub: 'Roles & scopes defined',
    monthlyTrafficTitle: 'Active Token Grants',
    monthlyTrafficSub: 'Issued in last 24h',
    
    // Health Banner
    systemHealthBanner: 'IAM Microservices Operating at 99.99% Stability',
    systemHealthSub: 'Auth server, Consul service discovery, and database replication are fully synchronized.',
    healthyBadge: '99.99% Healthy',
    
    // Charts & Server Health
    trafficChartTitle: 'Live Authentication Traffic & Tokens',
    trafficChartSub: 'Hourly throughput across Web, Mobile & Backend microservices',
    activeStreamers: '1,240 Active Sessions',
    serverHealthTitle: 'System & Infrastructure Health',
    dbPostgres: 'PostgreSQL Database Cluster',
    authServer: 'Spring Authorization Server (IAM)',
    gatewayServer: 'Spring Cloud Gateway API',
    kafkaBroker: 'Kafka Event Streaming Bus',
    statusExcellent: 'Excellent',
    statusRunning: 'Running',
    statusHealthy: 'Healthy',

    // Sidebar Sections
    sectionOverview: 'OVERVIEW',
    sectionIdentity: 'IDENTITY & RBAC',
    sectionSecurity: 'SECURITY & SESSIONS',
    sectionConfig: 'CONFIGURATION & AUDIT',
    
    tabOverview: 'Overview',
    tabAccount_security: 'Password & Security',
    tabInvitations: 'Invitations',
    tabDashboard: 'Admin Overview',
    tabUsers: 'Users',
    tabRoles: 'Roles (RBAC)',
    tabPermissions: 'Permissions Catalog',
    tabSessions: 'Active Sessions',
    tabLogins: 'Login Monitoring',
    tabSecurity: 'Security Operations',
    tabClients: 'OAuth2 Clients',
    tabUser_sessions: 'User Active Sessions',
    tabUser_logins: 'User Login History',
    tabAdmin_security: 'Account Security Ops',
    tabAudit: 'Audit Logs',
    tabConfig: 'System Policies',
    tabProfile: 'Profile Settings',
    tabLinked: 'Linked Accounts',
    tabHistory: 'Login History',
    tabActivity: 'Account Activity',
    tabDeactivation: 'Account Deactivation',
    accountSettings: 'Account Settings',
    accountSubtitle: 'Manage your personal profile, security credentials, linked accounts, and active sessions.',
    userSelfServiceBadge: 'USER SELF-SERVICE',
    logout: 'Sign Out',

    // Personal Profile Tab
    profileTitle: 'Personal Profile',
    profileSubtitle: 'Manage your user identity, contact information, personal details, and localization preferences.',
    adminAccountBadge: 'ADMIN ACCOUNT',
    userAccountBadge: 'USER ACCOUNT',
    supportedPhotoFormats: 'Supported photo formats: JPG, PNG, WebP (Square ratio recommended)',
    secNameIdentity: '1. Name & Identity',
    firstNameLabel: 'First Name (Given Name)',
    lastNameLabel: 'Last Name (Family Name)',
    middleNameLabel: 'Middle Name',
    middleNamePlaceholder: 'Middle name (optional)',
    nicknameLabel: 'Nickname',
    nicknamePlaceholder: 'Ram',
    usernameLabel: 'Username',
    emailLabel: 'Email Address',
    emailVerified: 'Verified',
    emailUnverified: 'Unverified',
    verifyEmailBtn: 'Verify Email',
    verifyEmailSuccess: 'Your email has been successfully verified!',
    secContactDetails: '2. Contact & Personal Details',
    phoneNumberLabel: 'Phone Number',
    genderLabel: 'Gender',
    selectGender: 'Select Gender',
    male: 'Male',
    female: 'Female',
    other: 'Other',
    birthdateLabel: 'Date of Birth',
    selectBirthdatePlaceholder: 'Select Date of Birth',
    secLocalization: '3. Localization & Online Presence',
    preferredLanguage: 'Preferred Language',
    khmerLang: 'Khmer (ភាសាខ្មែរ)',
    englishLang: 'English (EN)',
    timezoneLabel: 'Timezone',
    websiteUrlLabel: 'Website / Portfolio URL',
    profileUrlLabel: 'Profile Link (GitHub, LinkedIn)',
    saveChangesBtn: 'Save Profile Changes',
    savingBtn: 'Saving Changes...',
    profileSavedSuccess: 'Profile changes saved successfully!',
    profileSaveFailed: 'Failed to update profile.',

    // Common Action Buttons & Labels
    save: 'Save',
    cancel: 'Cancel',
    edit: 'Edit',
    delete: 'Delete',
    confirm: 'Confirm',
    search: 'Search',
    actions: 'Actions',
    status: 'Status',
    active: 'Active',
    disabled: 'Disabled',
    locked: 'Locked',
    all: 'All',
    close: 'Close',
    create: 'Create',
    add: 'Add',
    back: 'Back',
  },
  km: {
    // Brand & Navigation
    appTitle: 'ប្រព័ន្ធគ្រប់គ្រងអត្តសញ្ញាណ ISTAD',
    adminBreadcrumb: 'Admin',
    dashboardBreadcrumb: 'ផ្ទាំងគ្រប់គ្រង',
    accountPortal: 'គណនីផ្ទាល់ខ្លួន',
    adminPortal: 'ផ្ទាំងគ្រប់គ្រងប្រតិបត្តិការ',
    switchPortal: 'ប្តូរផ្ទាំង',
    language: 'ភាសា',
    
    // Live Dashboard Header
    liveMonitoringTag: 'ការត្រួតពិនិត្យប្រព័ន្ធផ្ទាល់',
    dashboardTitle: 'ផ្ទាំងគ្រប់គ្រងប្រតិបត្តិការ',
    dashboardSubtitle: 'ទិដ្ឋភាពទូទៅនៃប្រព័ន្ធ IAM អត្តសញ្ញាណអ្នកប្រើប្រាស់ សម័យការងារ និងចរាចរណ៍ផ្ទៀងផ្ទាត់ជាក់ស្តែង។',
    refreshData: 'ផ្ទុកទិន្នន័យឡើងវិញ',
    lastUpdated: 'ធ្វើបច្ចុប្បន្នភាពទើបតែមុននេះ',
    
    // 4 KPI Cards
    totalUsersTitle: 'អ្នកប្រើប្រាស់សរុប',
    totalUsersSub: 'ធៀបនឹងខែមុន',
    activeSessionsTitle: 'សម័យការងារសកម្ម',
    activeSessionsSub: 'ទិន្នន័យជាក់ស្តែង',
    rolesPermsTitle: 'តួនាទី & សិទ្ធិសរុប',
    rolesPermsSub: 'តួនាទី និងកាតាឡុកសិទ្ធិ',
    monthlyTrafficTitle: 'ការផ្ទៀងផ្ទាត់ Token សកម្ម',
    monthlyTrafficSub: 'ក្នុងរយៈពេល ២៤ ម៉ោងចុងក្រោយ',
    
    // Health Banner
    systemHealthBanner: 'ប្រព័ន្ធ IAM ដំណើរការប្រកបដោយស្ថិរភាព 99.98%',
    systemHealthSub: 'ម៉ាស៊ីនបម្រើការផ្ទៀងផ្ទាត់ OAuth2, API Gateway និង Database ដំណើរការយ៉ាងរលូន។',
    healthyBadge: '99.98% ស្ថិរភាពខ្ពស់',
    
    // Charts & Server Health
    trafficChartTitle: 'គំនូសតាងចរាចរណ៍ផ្ទៀងផ្ទាត់ & សម័យការងារ',
    trafficChartSub: 'ចំនួនសំណើផ្ទៀងផ្ទាត់តាមពេលវេលាជាក់ស្តែងលើ Web, Mobile និង Microservices',
    activeStreamers: '១,២៤០ សម័យការងារសកម្ម',
    serverHealthTitle: 'ស្ថានភាពប្រព័ន្ធ & ម៉ាស៊ីនបម្រើ',
    dbPostgres: 'មូលដ្ឋានទិន្នន័យ PostgreSQL (db_iam)',
    authServer: 'ប្រព័ន្ធសម្គាល់ Spring Auth Server',
    gatewayServer: 'ច្រកទ្វារប្រព័ន្ធ Spring Cloud Gateway',
    kafkaBroker: 'ប្រព័ន្ធសារ Kafka Event Bus',
    statusExcellent: 'ល្អឥតខ្ចោះ',
    statusRunning: 'ដំណើរការល្អ',
    statusHealthy: 'ល្អឥតខ្ចោះ',

    // Sidebar Sections
    sectionOverview: 'ទិដ្ឋទូទៅ',
    sectionIdentity: 'អត្តសញ្ញាណ & សិទ្ធិ',
    sectionSecurity: 'សុវត្ថិភាព & តាមដាន',
    sectionConfig: 'ការកំណត់ & សវនកម្ម',
    
    // Sidebar Tabs
    tabOverview: 'ទិដ្ឋភាពទូទៅ',
    tabAccount_security: 'ពាក្យសម្ងាត់ និងសុវត្ថិភាព',
    tabInvitations: 'ការអញ្ជើញ',
    tabDashboard: 'ផ្ទាំងគ្រប់គ្រង Admin',
    tabUsers: 'អ្នកប្រើប្រាស់',
    tabRoles: 'តួនាទី (Roles)',
    tabPermissions: 'សិទ្ធិអនុញ្ញាត',
    tabSessions: 'សម័យការងារសកម្ម',
    tabLogins: 'តាមដានការចូលប្រព័ន្ធ',
    tabSecurity: 'ប្រតិបត្តិការសុវត្ថិភាព',
    tabClients: 'កម្មវិធី OAuth2 Clients',
    tabUser_sessions: 'គ្រប់គ្រងសម័យការងារ',
    tabUser_logins: 'ត្រួតពិនិត្យការចូល',
    tabAdmin_security: 'ប្រតិបត្តិការសុវត្ថិភាព',
    tabAudit: 'កំណត់ហេតុសវនកម្ម',
    tabConfig: 'គោលការណ៍ប្រព័ន្ធ',
    tabProfile: 'ព័ត៌មានផ្ទាល់ខ្លួន',
    tabLinked: 'គណនីភ្ជាប់ (Social)',
    tabHistory: 'ប្រវត្តិការចូលប្រព័ន្ធ',
    tabActivity: 'សកម្មភាពគណនី',
    tabDeactivation: 'ស្នើសុំបិទគណនី',
    accountSettings: 'ការកំណត់គណនីផ្ទាល់ខ្លួន',
    accountSubtitle: 'គ្រប់គ្រងព័ត៌មានផ្ទាល់ខ្លួន ពាក្យសម្ងាត់ គណនីភ្ជាប់ និងសម័យការងារសកម្ម',
    userSelfServiceBadge: 'គណនីផ្ទាល់ខ្លួន',
    logout: 'ចាកចេញ',

    // Personal Profile Tab
    profileTitle: 'ព័ត៌មានផ្ទាល់ខ្លួន',
    profileSubtitle: 'គ្រប់គ្រងអត្តសញ្ញាណ ព័ត៌មានទំនាក់ទំនង ព័ត៌មានលម្អិត និងចំណូលចិត្តភាសា។',
    adminAccountBadge: 'គណនី ADMIN',
    userAccountBadge: 'គណនីទូទៅ',
    supportedPhotoFormats: 'ទម្រង់រូបភាពដែលគាំទ្រ៖ JPG, PNG, WebP (សមាមាត្រការ៉េត្រូវបានណែនាំ)',
    secNameIdentity: '១. ឈ្មោះ & អត្តសញ្ញាណ',
    firstNameLabel: 'នាមខ្លួន (First Name)',
    lastNameLabel: 'នាមត្រកូល (Last Name)',
    middleNameLabel: 'នាមកណ្តាល (Middle Name)',
    middleNamePlaceholder: 'នាមកណ្តាល (មិនទាមទារ)',
    nicknameLabel: 'ឈ្មោះហៅក្រៅ (Nickname)',
    nicknamePlaceholder: 'ឧ. Ram',
    usernameLabel: 'ឈ្មោះគណនី (Username)',
    emailLabel: 'អាសយដ្ឋានអ៊ីមែល (Email Address)',
    emailVerified: 'បានផ្ទៀងផ្ទាត់',
    emailUnverified: 'មិនទាន់ផ្ទៀងផ្ទាត់',
    verifyEmailBtn: 'ផ្ទៀងផ្ទាត់អ៊ីមែល',
    verifyEmailSuccess: 'អ៊ីមែលរបស់អ្នកត្រូវបានផ្ទៀងផ្ទាត់ជោគជ័យ!',
    secContactDetails: '២. ព័ត៌មានទំនាក់ទំនង & ជីវប្រវត្តិ',
    phoneNumberLabel: 'លេខទូរស័ព្ទ (Phone Number)',
    genderLabel: 'ភេទ (Gender)',
    selectGender: 'ជ្រើសរើសភេទ',
    male: 'ប្រុស',
    female: 'ស្រី',
    other: 'ផ្សេងៗ',
    birthdateLabel: 'ថ្ងៃខែឆ្នាំកំណើត (Date of Birth)',
    selectBirthdatePlaceholder: 'ជ្រើសរើសថ្ងៃខែឆ្នាំកំណើត',
    secLocalization: '៣. ចំណូលចិត្តភាសា & តំណភ្ជាប់អនឡាញ',
    preferredLanguage: 'ភាសាដែលពេញចិត្ត',
    khmerLang: 'ភាសាខ្មែរ (Khmer)',
    englishLang: 'អង់គ្លេស (EN)',
    timezoneLabel: 'តំបន់ពេលវេលា (Timezone)',
    websiteUrlLabel: 'គេហទំព័រ / ផលប័ត្រ (Website URL)',
    profileUrlLabel: 'តំណភ្ជាប់ប្រវត្តិរូប (GitHub, LinkedIn)',
    saveChangesBtn: 'រក្សាទុកការផ្លាស់ប្តូរ',
    savingBtn: 'កំពុងរក្សាទុក...',
    profileSavedSuccess: 'ការផ្លាស់ប្តូរត្រូវបានរក្សាទុកដោយជោគជ័យ!',
    profileSaveFailed: 'បរាជ័យក្នុងការធ្វើបច្ចុប្បន្នភាពប្រវត្តិរូប',

    // Common Action Buttons & Labels
    save: 'រក្សាទុក',
    cancel: 'បោះបង់',
    edit: 'កែប្រែ',
    delete: 'លុប',
    confirm: 'បញ្ជាក់',
    search: 'ស្វែងរក',
    actions: 'សកម្មភាព',
    status: 'ស្ថានភាព',
    active: 'សកម្ម',
    disabled: 'បានបិទ',
    locked: 'បានចាក់សោ',
    all: 'ទាំងអស់',
    close: 'បិទ',
    create: 'បង្កើត',
    add: 'បន្ថែម',
    back: 'ត្រឡប់ក្រោយ',
  }
};

const LanguageContext = createContext();

export function LanguageProvider({ children }) {
  const [locale, setLocale] = useState(() => localStorage.getItem('locale') || 'km');
  const [theme, setTheme] = useState(() => localStorage.getItem('theme') || 'light');
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [mobileSidebarOpen, setMobileSidebarOpen] = useState(false);

  useEffect(() => {
    localStorage.setItem('locale', locale);
  }, [locale]);

  useEffect(() => {
    localStorage.setItem('theme', theme);
    if (theme === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [theme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === 'light' ? 'dark' : 'light'));
  };

  const toggleSidebar = () => {
    setSidebarCollapsed((prev) => !prev);
  };

  const toggleMobileSidebar = () => {
    setMobileSidebarOpen((prev) => !prev);
  };

  const t = (key) => {
    return translations[locale]?.[key] || translations.km[key] || translations.en[key] || key;
  };

  return (
    <LanguageContext.Provider
      value={{
        locale,
        setLocale,
        t,
        theme,
        toggleTheme,
        sidebarCollapsed,
        toggleSidebar,
        mobileSidebarOpen,
        setMobileSidebarOpen,
        toggleMobileSidebar,
      }}
    >
      {children}
    </LanguageContext.Provider>
  );
}

export function useLanguage() {
  return useContext(LanguageContext);
}
