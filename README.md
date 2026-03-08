# InsightGuard 愈见🛡️

> **BUPT 2026 雏雁计划项目** > **定位**：基于毫米波雷达与 AI 大模型的 PTSD 患者远程居家守护系统。

## 🌟 项目愿景
针对 PTSD（创伤后应激障碍）患者在睡眠或独处时可能出现的惊恐发作（Panic Attack）或噩梦（Nightmare），系统通过无感监测生理指标，实时为家属提供可视化监控与 AI 驱动的专业干预建议，缓解家属焦虑，守护患者安全。

---

## 👥 团队分工 (Team Roles)

| 成员 | 角色 | 核心职责 |
| :--- | :--- | :--- |
| **郭宇泽 (Leader)** | **后端架构/安全** | 系统全链路设计、API 开发、BCrypt 隐私加密、AI SDK 集成。 |
| **张逸轩** | **后端开发/仿真** | 编写生理信号状态机（Simulator），模拟多模态发病数据。 |
| **贾若琦** | **AI 内容工程** | 调优专家级 Prompt，构建分级干预家属文案库，文档润色。 |
| **李思辰** | **前端开发** | 使用 Vue 3 + ECharts 构建家属监控大屏，实现实时预警交互。 |
| **杜如彧** | **UI/UX 设计** | 负责家属端高保真原型设计、硬件部署逻辑图、产品交互流程。 |

---

## 🛠️ 技术栈 (Tech Stack)

- **Backend**: Spring Boot 3, MySQL, MyBatis-Plus
- **Frontend**: Vue 3, Vite, ECharts, Axios
- **AI Engine**: DeepSeek API (LLM-based Analysis)
- **Collaboration**: Git + GitHub, VS Code, IntelliJ IDEA

---

## 📊 数据协议 (Data Specification)

项目统一采用 `snake_case`（下划线）命名法进行前后端数据交换：

| 字段名 | 意义 | 预警阈值 |
| :--- | :--- | :--- |
| `heart_rate` | 心率 (BPM) | > 110 触发预警 |
| `resp_rate` | 呼吸频率 | > 25 触发异常 |
| `motion_scale` | 体动强度 | 0.0 - 1.0 (雷达模拟) |
| `status_code` | 状态标签 | `NORMAL`, `STRESS`, `ATTACK` |
| `ai_insight` | AI 建议 | 针对家属的专业行动指南 |

---

## 📂 目录结构

```text
InsightGuard/
├── backend/            # Spring Boot 后端源码
├── frontend/           # Vue 前端源码
├── docs/               # 文档、设计图、Prompt 模板
└── sql/                # 数据库初始化脚本
