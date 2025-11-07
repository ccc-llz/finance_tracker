# [Back to README](../README.md)

# Working Features

Our finance tracker provides a complete solution for personal and collaborative financial management. Users can track expenses, set budgets, collaborate with others, visualize spending patterns, and receive AI-powered insights. The application includes gamification elements through achievements and a virtual store to keep users engaged. All features are fully functional and ready for daily use.

## Functional Features

### 1. Registration & Login

**User Registration & Login:**
- Create new accounts with email and password
- Login with JWT tokens
- Update email address
- Change password

### 2. Overall Navigation & Transaction Management

**Nav bar:**
- Quickly switch between pages

**Transaction Management:**
- Create income and expense transactions
- Set transaction name, amount, date, and category
- Link transactions to specific accounts and ledgers
- Support multiple currency types (AUD, USD, EUR, etc.)
- Create recurring transactions (daily, weekly, monthly, yearly, or decadal)
- Optional end dates for recurring transactions
- Automatic transaction generation based on schedule

**Ledger Switch:**
- Switch between ledgers globally and quickly browse the records in each ledger

### 3. Dashboard

The Dashboard shows a quick summary of users weekly financial activity with charts, transaction lists, and AI insights.

**AI-Powered Insights:**
- Pattern Analysis:
  - Identify the user's past spending patterns, using up to 90 days of data
    - Analyze the average amount, frequency, fluctuations, and trends for each category, as well as total spending
  - Analyze the user's current spending (default: weekly)
    - Compare by category with historical data to find similarities and differences in spending patterns
    - Compare with historical trends and fluctuations to detect unusual spending

- AI Recommendations:
  - Personalized consumption advice based on Pattern Analysis
    - Turn the previous analysis into a text prompt
    - Focus on total spending, category distribution, and unusual transactions
    - The tone is warm, cute, and professional

**Weekly Data Visualization:**
- Weekly expense barchart (expense vs budget)
- Transaction table for current week
- Spending trend charts (income vs expense)
- Pie chart for category breakdown

### 4. Insights

The Insights page offers comprehensive financial analytics with flexible date range selection.

**Date Range Selection:**
- Custom date picker with week, month and year view modes

**Data Visualization:**
- Show data for the selected time period
- Smart Bar Chart
  - Automatically chooses best visualization based on date range
- Smart Spending trend charts (income vs expense)
  - Automatically chooses best visualization based on date range
- Pie chart for category breakdown
- Transaction table

### 5. Accounts

Users can track balances, view transactions for each account, and organize your financial accounts.

**Bank Account Tracking:**
- Create multiple accounts (e.g., savings, checking, credit cards)
- Set initial balance for each account
- Track account balances automatically
- Support different currencies per account
- Card number support for credit/debit cards

**Account Management:**
- View all accounts in organized lists
- Select account to view its transactions
- View account expense and income totals
- Activate or deactivate accounts
- See transaction history per account

### 6. Ledgers

Users can create personal or collaborative ledgers, invite others, and manage permissions.

**Ledger Creation:**
- Create personal ledgers for individual use
- Create collaborative ledgers for shared finances
- Set ledger budgets during creation
- Choose default currency for each ledger
- Set ledger names and types

**Ledger Management:**
- View all ledgers (owned and shared)
- See ledger balance, income, and expense totals
- Activate or deactivate ledgers
- Track ledger budgets vs actual spending
- Switch between ledgers

**Collaboration Features:**
- Invite other users to collaborate on ledgers
- Send invitations via email
- Accept or reject ledger invitations
- View pending invitations with badge notifications
- Set edit permissions for collaborators
- Set read-only permissions for viewers
- Revoke collaborator access
- View all collaborators for each ledger
- Check if users are available for invitation

### 7. Budgets

Users can set overall monthly budgets and category-specific budgets to stay on track with your financial goals.

**Overall Budget:**
- Set monthly budgets for entire ledgers
- Track total spending vs budget
- See remaining budget amount
- Calculate daily budget allowance
- View budget usage percentage

**Category Budgets:**
- Set budgets for specific expense categories
- Track spending per category
- Compare category spending to budget limits
- Create, update, and delete category budgets
- Visual indicators for budget status

**Budget Visualization:**
- Overall budget overview cards
- Category budget cards with spending progress
- Expense barchart (expense vs budget)
- Spending trend charts (income vs expense)
- Pie chart for category breakdown

### 8. Achievements

Users can unlock achievements by reaching milestones, and use tokens to purchase themes and customize the application.

**Achievement System:**
- Track achievements
  - Achievement overview statistics
  - View achievement unlock conditions
- Achievement reward points
- Toast notifications when achievements unlock

**Virtual Store:**
- Themes as goods
- Purchase items using tokens

### 9. Settings

**Profile Management:**
- Update email addresses

**Security Settings:**
- Change password

**Data Export:**
- Export transactions as CSV files
- Export transactions as PDF reports

**Theme Customization:**
- Customize entire app appearance
- Customize transaction button icons

**Account Actions:**
- Sign out from account

### 10. Admin Panel
- List and manage store products


## Non-Functional Features

### 1. Security & Authentication

- JWT-based authentication with stateless sessions
- Password encryption using BCrypt (12 rounds)
- CORS configuration for secure cross-origin requests
- Role-based access control (USER, ADMIN)
- Session management with stateless JWT tokens

### 2. Performance & Scalability

- Docker containerization for easy deployment and scaling
- Microservices architecture (backend, frontend, AI service)
- Resource limits and reservations for AI service

### 3. Usability & User Experience

- Responsive design for mobile and desktop devices
  - The page is divided into three areas: left, center, and right. The left is the navigation bar, the center shows main information, and the right shows secondary information
  - In mobile view, the left and right panels collapse and can be toggled by clicking
  - The "new transaction" button, originally in the navigation bar, is centered for easier operation on small screens
- LocalStorage persistence for user preferences
- Theme customization for personalization

### 4. Reliability & Maintainability

- Docker Compose orchestration for multi-service deployment
- Health checks for all services (backend, frontend, database, Redis, AI)
- Structured logging and error handling
- Database persistence with volume mounting
- Modular code architecture for maintainability
